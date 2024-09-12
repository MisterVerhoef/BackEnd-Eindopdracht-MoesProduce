package novi.backend.eindopdrachtmoesproducebackend.exceptions;

public class AdvertNotFoundException extends RuntimeException  {

    public AdvertNotFoundException(Long id) {

        super("Advert with id " + id + " not found.");

    }

}
