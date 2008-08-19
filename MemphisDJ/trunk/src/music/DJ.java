package music;

public class DJ {

	private Lackey lackey;
	private Playlist playlist;
	
	public DJ (){
		lackey = new Lackey(this);
		playlist = new Playlist();
	}
	
	
}
