/*
 * =====================================================================
 *  Iterator Design Pattern                              LLD | Easy
 * =====================================================================
 *
 * PATTERN
 *   Iterator - Behavioral family (GoF). Also called Cursor.
 *
 * INTENT
 *   Give clients a uniform way to walk the elements of a collection one at a time
 *   without exposing how the collection stores them. The traversal position lives in
 *   the iterator, not in the collection and not in the client.
 *
 * WHEN TO USE, WHEN NOT
 *   Use when: callers should loop over your aggregate (playlist, paged API result,
 *     tree, result set) while you stay free to change the backing store from an
 *     ArrayList to a tree or a stream of pages.
 *   Do not use when: a plain List getter is honest and enough - a hand-rolled iterator
 *     over an ArrayList that you already expose buys nothing. In real Java you almost
 *     always implement java.lang.Iterable instead of inventing the interface.
 *
 * ROLES IN THIS CODE
 *   Iterator<T>        -> Iterator interface (hasNext / next)
 *   PlayListIterator   -> ConcreteIterator: owns the position index
 *   SongCollection     -> Aggregate interface (createIterator)
 *   PlayList           -> ConcreteAggregate: holds List<Song>, hands out iterators
 *   Song               -> the element being traversed
 *   IteratorPatternExample.main -> Client: uses only hasNext/next
 *
 * KEY INSIGHT
 *   Externalising the cursor is what lets several traversals run over the same
 *   collection at once - each createIterator() call returns a fresh, independent
 *   position. If the index lived in PlayList, two loops would fight over one counter.
 *   This is the first step of "pull behaviour out of the collection"; Visitor is the
 *   same idea pushed all the way to the operation itself.
 *
 * COMPLEXITY
 *   Time  O(1) per next(), O(n) for a full pass over n songs.
 *   Space O(1) per iterator - a reference plus one int index.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why implement Iterable<Song> rather than a custom interface? (for-each support)
 *   - Fail-fast vs fail-safe: ArrayList's iterator throws ConcurrentModificationException
 *     via a modCount check; CopyOnWriteArrayList iterates a snapshot instead. This
 *     hand-rolled iterator is neither - mutating the playlist mid-loop silently skips.
 *   - Internal vs external iteration: forEach/Stream (collection drives) vs hasNext/next
 *     (client drives). Which allows early exit, laziness, parallelism?
 *   - How would you iterate something infinite or paged lazily?
 *
 * RUN
 *   main() runs 4 cases: a full traversal, two independent cursors over one playlist,
 *   an empty playlist, and calling next() past the end. Each prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

interface Iterator<T> {
    boolean hasNext();

    T next();
}

interface SongCollection {
    Iterator<Song> createIterator();
}

class Song {

    private final String title;
    private final String artist;

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

    @Override
    public String toString() {
        return title + " by " + artist;
    }
}

class PlayList implements SongCollection {

    private final List<Song> songs = new ArrayList<>();

    public void addSong(Song song) {
        songs.add(song);
    }

    /** Every call hands back a brand new cursor starting at position 0. */
    @Override
    public Iterator<Song> createIterator() {
        return new PlayListIterator(songs);
    }
}

class PlayListIterator implements Iterator<Song> {

    private final List<Song> songs;
    private int position = 0;   // the traversal state that the aggregate no longer carries

    public PlayListIterator(List<Song> songs) {
        this.songs = songs;
    }

    @Override
    public boolean hasNext() {
        return position < songs.size();
    }

    @Override
    public Song next() {
        // Contract of java.util.Iterator: next() past the end throws, it does not return null.
        if (!hasNext()) {
            throw new NoSuchElementException("no more songs in the playlist");
        }
        return songs.get(position++);
    }
}

class IteratorPatternExample {

    public static void main(String[] args) {
        PlayList playList = new PlayList();
        playList.addSong(new Song("Blinding Lights", "The Weeknd"));
        playList.addSong(new Song("Viva La Vida", "Coldplay"));
        playList.addSong(new Song("Shape of You", "Ed Sheeran"));

        // Case 1: a full pass through the client-facing interface only.
        print("case 1 full traversal", drain(playList.createIterator()),
                "[Blinding Lights by The Weeknd, Viva La Vida by Coldplay, "
                        + "Shape of You by Ed Sheeran]");

        // Case 2 (tricky): two cursors over the same playlist do not share a position.
        Iterator<Song> first = playList.createIterator();
        Iterator<Song> second = playList.createIterator();
        first.next();                       // advance only the first cursor
        print("case 2 independent cursors", first.next() + " | " + second.next(),
                "Viva La Vida by Coldplay | Blinding Lights by The Weeknd");

        // Case 3 (edge): an empty aggregate - hasNext() is false straight away.
        print("case 3 empty playlist", drain(new PlayList().createIterator()), "[]");

        // Case 4 (edge): next() past the end must throw, not return null.
        String result;
        try {
            Iterator<Song> exhausted = new PlayList().createIterator();
            result = String.valueOf(exhausted.next());
        } catch (NoSuchElementException e) {
            result = "NoSuchElementException: " + e.getMessage();
        }
        print("case 4 next past end", result,
                "NoSuchElementException: no more songs in the playlist");
    }

    /** Walks an iterator to exhaustion, collecting what it yields. */
    private static List<String> drain(Iterator<Song> iterator) {
        List<String> collected = new ArrayList<>();
        while (iterator.hasNext()) {
            collected.add(iterator.next().toString());
        }
        return collected;
    }

    private static void print(String label, Object actual, Object expected) {
        boolean ok = String.valueOf(actual).equals(String.valueOf(expected));
        System.out.println(label + ": " + actual + "   expected " + expected
                + "   " + (ok ? "[OK]" : "[FAIL]"));
    }
}
