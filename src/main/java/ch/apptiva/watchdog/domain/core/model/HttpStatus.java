package ch.apptiva.watchdog.domain.core.model;

import ch.apptiva.watchdog.domain.shared.ValueObject;

public class HttpStatus extends ValueObject {

    private final int status;

    public HttpStatus(int status) {
        this.status = status;
    }

    public boolean isGood() {
        return status >= 200 && status < 400;
    }

    @Override
    public int hashCode() {
        return status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof HttpStatus) {
            HttpStatus other = (HttpStatus) obj;
            return status == other.status;
        } else {
            return false;
        }
    }
}
