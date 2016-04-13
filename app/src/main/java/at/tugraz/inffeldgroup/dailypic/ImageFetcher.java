package at.tugraz.inffeldgroup.dailypic;

/**
 * Created by markus on 13/04/16.
 */
public class ImageFetcher {
    private static ImageFetcher ourInstance = new ImageFetcher();

    public static ImageFetcher getInstance() {
        return ourInstance;
    }

    private ImageFetcher() {
    }
}
