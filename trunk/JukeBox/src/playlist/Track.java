package playlist;

public class Track {
	private final String title;
	private final String artist;
	private final String album;
	private final String genre;
	private final int time;
	
	public Track(){
		title = "The title";
		artist = "The artist";
		album = "The album";
		genre = "The genre";
		time = 200;
	}
	public String getAlbum() {
		return album;
	}
	public String getArtist() {
		return artist;
	}

	public String getGenre() {
		return genre;
	}
	public String getTitle() {
		return title;
	}
	/*
	public String getTrack() {
		return track;
	}
	public String getYear() {
		return year;
	}
	public String getComment() {
		return comment;
	}
	*/
	public String toString(){
		return title + " - " + artist;
	}
}
