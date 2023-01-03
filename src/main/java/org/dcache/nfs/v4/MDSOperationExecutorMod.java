package org.dcache.nfs.v4;

import org.dcache.nfs.v4.xdr.nfs_argop4;
import org.dcache.nfs.v4.xdr.nfs_opnum4;


public class MDSOperationExecutorMod extends MDSOperationExecutor {
    @Override
    protected AbstractNFSv4Operation getOperation(nfs_argop4 op) {
        if (op.argop == nfs_opnum4.OP_GETATTR) {
            return new OperationGETATTRMod(op);
        }
        return super.getOperation(op);
    }
}