package dev.jd.multihardcore;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

public class DiscordBot {
	private String token;
	private JDA bot;
	private long minecraftChannelID;

	public DiscordBot(String token, long minecraftChannelID) 
		throws InterruptedException {
		this.token = token;
		this.minecraftChannelID = minecraftChannelID;
		bot = JDABuilder.createDefault(token).build();
		bot.awaitReady();
	}

	public void sendMessage(String message) {
		ThreadChannel threadChannel = bot.getThreadChannelById(minecraftChannelID);
		TextChannel textChannel = bot.getTextChannelById(minecraftChannelID);

		if (threadChannel != null) {
			threadChannel.sendMessage(message).queue();
		}
		if (textChannel != null) {
			textChannel.sendMessage(message).queue();
		}
	}

	public void sendEmbed(MessageEmbed embed) {
		ThreadChannel threadChannel = bot.getThreadChannelById(minecraftChannelID);
		TextChannel textChannel = bot.getTextChannelById(minecraftChannelID);

		if (threadChannel != null) {
			threadChannel.sendMessageEmbeds(embed).queue();
		}
		if (textChannel != null) {
			textChannel.sendMessageEmbeds(embed).queue();
		}
	}

	public String getRoleMention(long roleId) {
		ThreadChannel textChannel = bot.getThreadChannelById(minecraftChannelID);
		Role role = textChannel.getGuild().getRoleById(roleId);
		if (role != null)
			return role.getAsMention();
		return "";
	}

}
