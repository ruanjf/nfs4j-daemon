package org.dcache.nfs.v4;

import org.dcache.nfs.nfsstat;
import org.dcache.nfs.v4.xdr.GETATTR4res;
import org.dcache.nfs.v4.xdr.GETATTR4resok;
import org.dcache.nfs.v4.xdr.nfs_argop4;
import org.dcache.nfs.v4.xdr.nfs_resop4;
import org.dcache.nfs.vfs.PseudoFs;
import world.gfi.nfs4j.fs.RootFileSystem;

import java.io.IOException;
import java.lang.reflect.Field;

public class OperationGETATTRMod extends OperationGETATTR {
    public OperationGETATTRMod(nfs_argop4 args) {
        super(args);
    }

    @Override
    public void process(CompoundContext context, nfs_resop4 result) throws IOException {

        final GETATTR4res res = result.opgetattr;
        RootFileSystem fs = getInnerFs((PseudoFs) context.getFs());
        res.resok4 = new GETATTR4resok();
        res.resok4.obj_attributes = getAttributes(_args.opgetattr.attr_request,
                fs.delegate(context.currentInode()),
                context.currentInode(), context);

        res.status = nfsstat.NFS_OK;

    }

    private RootFileSystem getInnerFs(PseudoFs fs) {
        try {
            Field field = fs.getClass().getDeclaredField("_inner");
            //设置对象的访问权限，保证对private的属性的访问
            field.setAccessible(true);
            return  (RootFileSystem) field.get(fs);
        } catch (Exception e) {
            return null;
        }
    }
}
