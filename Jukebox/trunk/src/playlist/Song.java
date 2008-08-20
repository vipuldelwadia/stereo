package playlist;

public class Song {
    
    private static final String EMPTY = ""; //$NON-NLS-1$
    
	private final String title;
	private final String artist;
	private final String album;
	private final String genre;
	private final int seconds;
	
	public Song(final String _title, final String _artist, final String _album, final String _genre, final int _seconds) {
        super();
        this.title = (_title != null)?_title:EMPTY;
        this.artist = (_artist != null)?_artist:EMPTY;
        this.album = (_album != null)?_album:EMPTY;
        this.genre = (_genre != null)?_genre:EMPTY;
        this.seconds = _seconds;
    }

    /**
     * @return the album
     */
    public String getAlbum() {
        return this.album;
    }

    /**
     * @return the artist
     */
    public String getArtist() {
        return this.artist;
    }

    /**
     * @return the genre
     */
    public String getGenre() {
        return this.genre;
    }

    /**
     * @return the seconds
     */
    public int getSeconds() {
        return this.seconds;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }
    
    /**
     * @return true if the time is known
     */
    public boolean hasValidTime(){
        return this.getSeconds() >= 0;
    }
    
    @Override
    @SuppressWarnings("nls")
    public String toString(){
        String song = this.title + " - " + this.artist;
        if (this.album.length() > 0){
            song += " (" + this.album + ")";
        }
        return song;
    }
    
    public boolean equals(Song other){
        return false;
    }
}
