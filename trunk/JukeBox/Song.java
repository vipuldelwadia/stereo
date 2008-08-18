/**
 * header 	3 	"TAG"
title 	30 	30 characters of the title
artist 	30 	30 characters of the artist name
album 	30 	30 characters of the album name
year 	4 	A four-digit year
comment 	28[1] or 30 	The comment.
zero-byte[1] 	1 	If a track number is stored, this byte contains a binary 0.
track[1] 	1 	The number of the track on the album, or 0. Invalid, if previous byte is not a binary 0.
genre 	1 	Index in a list of genres, or 255
 * @author coxdyla
 *Description of a song
 */
public class Song {
	private String title;
	private String artist;
	private String album;
	private String year;
	private String comment;
	private String track;
	private String genre;
	
	public Song(String id3){
		//TODO parse id3 tags
	}
	public String getAlbum() {
		return album;
	}
	public String getArtist() {
		return artist;
	}
	public String getComment() {
		return comment;
	}
	public String getGenre() {
		return genre;
	}
	public String getTitle() {
		return title;
	}
	public String getTrack() {
		return track;
	}
	public String getYear() {
		return year;
	}
	public String toString(){
		return title + " - " + artist;
	}
}
