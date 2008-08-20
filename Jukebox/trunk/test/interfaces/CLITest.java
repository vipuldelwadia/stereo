package interfaces;

import org.junit.Before;
import org.junit.Test;

import player.Controller;

import interfaces.CLI;

public class CLITest {
    CLI cli;
    
    @Before
    public void testInstantiation() {
        cli = new CLI(new Controller());
    }
    
    @Test
    public void testinput() {
        cli.input("set volume 0");
        cli.input("set volume 10");
    }
    
    @Test
    public void testNegativeVolume() {
        cli.input("set volume -1");
    }
    
    @Test
    public void testTooHighVolume() {
        cli.input("set volume 11");
    }
    
    @Test
    public void testNotNumberVolume() {
        cli.input("set volume a");
    }
    
    @Test
    public void play() {
        cli.input("play");
    }
    
    @Test
    public void pause() {
        cli.input("pause");
    }
    
    @Test
    public void skip() {
        cli.input("skip");
    }
    
    @Test
    public void getVolume() {
        cli.input("get volume");
    }
    
    @Test
    public void playlist() {
        cli.input("playlist -1");
        cli.input("playlist 0");
        cli.input("playlist 10");
        
    }
    
    @Test
    public void testOtherInput() {
        cli.input("");
        cli.input("123");
        cli.input("get volume23");
    }
    
}