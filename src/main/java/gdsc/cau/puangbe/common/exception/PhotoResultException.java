package gdsc.cau.puangbe.common.exception;

import gdsc.cau.puangbe.common.util.ResponseCode;

public class PhotoResultException extends BaseException {

    public PhotoResultException(ResponseCode responseCode) {
        super(responseCode);
    }
}
