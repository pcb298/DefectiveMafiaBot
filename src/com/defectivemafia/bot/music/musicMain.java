package com.defectivemafia.bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import net.dv8tion.jda.core.entities.User;

public class musicMain {
  /*public static void main(String[] args) throws Exception {
    JDA jda = new JDABuilder(AccountType.BOT)
        .setToken(System.getProperty("botToken"))
        .buildBlocking();

    jda.addEventListener(new musicMain());
  }*/

  private final AudioPlayerManager playerManager;
  private final Map<Long, GuildMusicManager> musicManagers;
  public LinkedList<String> songList = new LinkedList<String>();
  public String curTrack = null;
  

  public musicMain() {
    this.musicManagers = new HashMap<>();

    this.playerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(playerManager);
    AudioSourceManagers.registerLocalSource(playerManager);
  }

  private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
    long guildId = Long.parseLong(guild.getId());
    GuildMusicManager musicManager = musicManagers.get(guildId);

    if (musicManager == null) {
      musicManager = new GuildMusicManager(playerManager);
      musicManagers.put(guildId, musicManager);
    }

    guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

    return musicManager;
  }

 /* @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    String[] command = event.getMessage().getContentRaw().split(" ", 2);
    Guild guild = event.getGuild();

    if (guild != null) {
      if ("~play".equals(command[0]) && command.length == 2) {
        loadAndPlay(event.getTextChannel(), command[1]);
      } else if ("~skip".equals(command[0])) {
        skipTrack(event.getTextChannel());
      }
    }

    super.onMessageReceived(event);
  }*/
  
  
  
  

  public void loadAndPlay(final TextChannel channel, final String trackUrl, Guild g, User user) {
    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
     
     

    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
      
      @Override
      public void trackLoaded(AudioTrack track) {
        channel.sendMessage("Adding to queue " + track.getInfo().title).queue();
        songList.add(track.getInfo().title);
        curTrack = track.getInfo().title;
        channel.sendMessage("Current song list: " + songList).queue();
        
        
        play(channel.getGuild(), musicManager, track, g, user);
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if (firstTrack == null) {
          firstTrack = playlist.getTracks().get(0);
        }

        channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

        play(channel.getGuild(), musicManager, firstTrack, g, user);
      }

      @Override
      public void noMatches() {
        channel.sendMessage("Nothing found by " + trackUrl).queue();
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        channel.sendMessage("Could not play: " + exception.getMessage()).queue();
      }
    });
  }

  private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, Guild g, User user) {
    connectToFirstVoiceChannel(guild.getAudioManager(), g, user);
    

    musicManager.scheduler.queue(track);
    //songList.remove(curTrack);
  }

  public void skipTrack(TextChannel channel) {
    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
    musicManager.scheduler.nextTrack();
    

    channel.sendMessage("Skipped to next track.").queue();
  }
  

  private static void connectToFirstVoiceChannel(AudioManager audioManager, Guild guild, User user) {
    if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
      for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
          
          
        voiceChannel = guild.getMember(user).getVoiceState().getChannel();  
        
        
        audioManager.openAudioConnection(voiceChannel);
        break;
      }
    }
  }
}