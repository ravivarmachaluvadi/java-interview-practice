import java.util.ArrayList;
import java.util.List;

interface Iterator<T> {
    boolean hasNext();

    T next();
}

interface SongCollection {
    Iterator<Song> createIterator();
}

class Song {
    private String title;
    private String artist;

    public Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

}

class PlayList implements SongCollection {

    private List<Song> songs = new ArrayList<>();

    public void addSong(Song song) {
        songs.add(song);
    }

    @Override
    public Iterator<Song> createIterator() {
        return new PlayListIterator(songs);
    }
}

class PlayListIterator implements Iterator<Song> {
    private List<Song> songs;
    private int position = 0;

    public PlayListIterator(List<Song> songs) {
        this.songs = songs;
    }

    @Override
    public boolean hasNext() {
        return position < songs.size();
    }

    @Override
    public Song next() {
        return songs.get(position++);
    }
}

class IteratorPatternExample {

    public static void main(String[] args) {
        PlayList playList = new PlayList();
        playList.addSong(new Song("Blinding Lights", "The Weeknd"));
        playList.addSong(new Song("Viva La Vida", "Coldplay"));
        playList.addSong(new Song("Shape of You", "Ed Sheeran"));

        Iterator<Song> iterator = playList.createIterator();
        while (iterator.hasNext()) {
            Song song = iterator.next();
            System.out.println(song.getTitle() + " by " + song.getArtist());
        }

    }

}
