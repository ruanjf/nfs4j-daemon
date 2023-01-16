package org.dcache.nfs.v3;

import org.dcache.nfs.ExportTable;
import org.dcache.nfs.nfsstat;
import org.dcache.nfs.v3.xdr.*;
import org.dcache.nfs.vfs.FsStat;
import org.dcache.nfs.vfs.Inode;
import org.dcache.nfs.vfs.PseudoFs;
import org.dcache.nfs.vfs.VirtualFileSystem;
import org.dcache.oncrpc4j.rpc.RpcCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.gfi.nfs4j.fs.RootFileSystem;

public class NfsServerV3Mod extends NfsServerV3 {
    private final RootFileSystem _vfs;
    private final ExportTable _exports;
    private static final Logger _log = LoggerFactory.getLogger(NfsServerV3Mod.class);

    public NfsServerV3Mod(ExportTable exports, RootFileSystem fs) {
        super(exports, fs);
        _vfs = fs;
        _exports = exports;
    }

    @Override
    public FSSTAT3res NFSPROC3_FSSTAT_3(RpcCall call$, FSSTAT3args arg1) {
        FSSTAT3res res = new FSSTAT3res();

        try {
            Inode inode = new Inode(arg1.fsroot.data);
            VirtualFileSystem fs = new PseudoFs(_vfs.delegate(inode), call$, _exports);
            res.status = nfsstat.NFS_OK;
            res.resok = new FSSTAT3resok();

            FsStat fsStat = fs.getFsStat();
            res.resok.tbytes = new size3(fsStat.getTotalSpace());
            res.resok.fbytes = new size3(fsStat.getTotalSpace() - fsStat.getUsedSpace());
            res.resok.abytes = new size3(fsStat.getTotalSpace() - fsStat.getUsedSpace());

            res.resok.tfiles = new size3(fsStat.getTotalFiles());
            res.resok.ffiles = new size3(fsStat.getTotalFiles() - fsStat.getUsedFiles());
            res.resok.afiles = new size3(fsStat.getTotalFiles() - fsStat.getUsedFiles());

            res.resok.invarsec = new uint32(0);

            res.resok.obj_attributes = new post_op_attr();
            res.resok.obj_attributes.attributes_follow = true;
            res.resok.obj_attributes.attributes = new fattr3();

            Utils.fill_attributes(fs.getattr(inode), res.resok.obj_attributes.attributes);

        } catch (Exception e) {
            _log.error("FSSTAT", e);
            res.status = nfsstat.NFSERR_SERVERFAULT;
            res.resfail = new FSSTAT3resfail();
            res.resfail.obj_attributes = Utils.defaultPostOpAttr();
        }

        return res;

    }
}
