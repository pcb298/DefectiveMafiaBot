package com.defectivemafia.bot.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;


public class musicControl {
    
    public static musicMain musicprog = new musicMain();
    
    
    
    
    
    public static void musicPlayer(String[] s, MessageReceivedEvent e){
        String requestAppend = "";
        for(int i = 2; i < s.length; i++)
            requestAppend += s[i]+" ";  
        if(requestAppend.length() > 0)
        requestAppend = requestAppend.substring(0, requestAppend.length() - 1);
        System.out.println("Song request:"+ requestAppend+"spot");
        
        switch(s[1]){
         case "play":{
        //music.play(e.getGuild(),e.getTextChannel(),e.getAuthor(),requestAppend);
        //musicprog.getGuildAudioPlayer(e.getGuild());
        musicprog.loadAndPlay(e.getTextChannel(), requestAppend, e.getGuild(),e.getAuthor() );
         }
         break;
         
        case "skip":
         musicprog.skipTrack(e.getTextChannel());
         break;
        }
        
    }
    
    
     
}
