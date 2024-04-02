import java.util.ArrayList;
import java.util.List;

public class FileTracker {

    public String filename;
    public List<WordInfoTracker> infoTrackers;

    FileTracker(String file) {
        filename = file;
        infoTrackers = new ArrayList<WordInfoTracker>();

    }

    public void AddTracker(WordInfoTracker tracker) {
        infoTrackers.add(tracker);
    }

}
