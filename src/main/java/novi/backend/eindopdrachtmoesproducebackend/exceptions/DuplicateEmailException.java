package novi.backend.eindopdrachtmoesproducebackend.exceptions;



public class DuplicateEmailException extends RuntimeException  {

    public DuplicateEmailException(String email) {

        super(email + " een account met dit emailadres bestaat al."  );

    }

}
