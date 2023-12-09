package dev.jd.multihardcore;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

public class DiscordBot {
	private String token;
	private JDA bot;
	private String minecraftChannelID;

	public DiscordBot(String token, String minecraftChannelID) 
		throws InterruptedException {
		this.token = token;
		this.minecraftChannelID = minecraftChannelID;
		bot = JDABuilder.createDefault(token).build();
		bot.awaitReady();
	}

	public void sendMessage(String message) {
		ThreadChannel textChannel = bot.getThreadChannelById(minecraftChannelID);
		if (textChannel != null) {
			textChannel.sendMessage(message).queue();
		}
	}

}
