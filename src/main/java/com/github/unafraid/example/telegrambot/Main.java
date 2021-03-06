package com.github.unafraid.example.telegrambot;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import com.github.unafraid.example.telegrambot.handlers.ExampleInlineMenuHandler;
import com.github.unafraid.example.telegrambot.handlers.HelpHandler;
import com.github.unafraid.example.telegrambot.handlers.StartCommandHandler;
import com.github.unafraid.example.telegrambot.handlers.WhoAmIHandler;
import com.github.unafraid.example.telegrambot.validators.AdminIdValidator;
import com.github.unafraid.telegrambot.bots.DefaultTelegramBot;

/**
 * @author UnAfraid
 */
public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	private static final String TOKEN = System.getenv("EXAMPLE_TG_BOT_TOKEN");
	private static final String USERNAME = System.getenv("EXAMPLE_TG_BOT_USERNAME");
	private static final String ADMIN_IDS = System.getenv("EXAMPLE_TG_BOT_ADMIN_IDS");
	
	public static void main(String[] args) throws Exception {
		if (TOKEN == null || TOKEN.isBlank()) {
			LOGGER.warn("EXAMPLE_TG_BOT_TOKEN is not defined!");
			return;
		} else if (USERNAME == null || USERNAME.isBlank()) {
			LOGGER.warn("EXAMPLE_TG_BOT_USERNAME is not defined!");
			return;
		} else if (ADMIN_IDS == null || ADMIN_IDS.isBlank()) {
			LOGGER.warn("EXAMPLE_TG_BOT_ADMIN_IDS is not defined!");
			return;
		}
		
		LOGGER.info("Initializing {} ...", USERNAME);
		
		final List<Integer> adminIds = parseAdminIds();
		if (adminIds.isEmpty()) {
			LOGGER.warn("Couldn't find admin ids");
			return;
		}
		LOGGER.info("Authorized admin ids: {}", adminIds);
		
		// Initialize API Context
		ApiContextInitializer.init();
		
		// Create new instance of TelegramBotsAPI
		final TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		
		// Register the default bot with token and username
		final DefaultTelegramBot telegramBot = new DefaultTelegramBot(TOKEN, USERNAME);
		telegramBotsApi.registerBot(telegramBot);
		
		// Register access level validator
		telegramBot.setAccessLevelValidator(new AdminIdValidator(adminIds));
		
		// Register handlers
		telegramBot.addHandler(new ExampleInlineMenuHandler());
		telegramBot.addHandler(new HelpHandler());
		telegramBot.addHandler(new StartCommandHandler());
		telegramBot.addHandler(new WhoAmIHandler());
		LOGGER.info("Initialization done");
	}
	
	private static List<Integer> parseAdminIds() {
		final List<Integer> whitelistUserIds = new ArrayList<>();
		for (String adminIdValue : ADMIN_IDS.split(",")) {
			try {
				final int adminId = Integer.parseInt(adminIdValue);
				if (adminId < 0) {
					LOGGER.warn("User ID expected, negative ids are reserved for groups!");
					continue;
				}
				whitelistUserIds.add(adminId);
			} catch (Exception e) {
				LOGGER.warn("Failed to parse admin id {}", adminIdValue, e);
			}
		}
		return whitelistUserIds;
	}
}
