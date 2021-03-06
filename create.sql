USE defectivemafiabot;

CREATE TABLE IF NOT EXISTS user (
	discord_id CHAR(18),
	date_updated TIMESTAMP 
		DEFAULT CURRENT_TIMESTAMP
		ON UPDATE CURRENT_TIMESTAMP,
	date_created TIMESTAMP 
		DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(discord_id)
);

CREATE TABLE IF NOT EXISTS identity (
	id INT NOT NULL AUTO_INCREMENT,
	user_id CHAR(18),
	type TINYINT,
	value VARCHAR(18),
	date_updated TIMESTAMP 
		DEFAULT CURRENT_TIMESTAMP
		ON UPDATE CURRENT_TIMESTAMP,
	date_created TIMESTAMP 
		DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(id),
	FOREIGN KEY (user_id) REFERENCES user (discord_id)
		ON UPDATE CASCADE
		ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS channel (
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(64),
	channel_id CHAR(18),
	date_updated TIMESTAMP 
		DEFAULT CURRENT_TIMESTAMP
		ON UPDATE CURRENT_TIMESTAMP,
	date_created TIMESTAMP 
		DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS event (
	id INT NOT NULL AUTO_INCREMENT,
	user_id CHAR(18),
	name VARCHAR(128),
	description VARCHAR(512),
	time DATETIME,
	should_repeat TINYINT(1) DEFAULT 0,
	repeat_after BIGINT,
	channel_id INT,
	mention CHAR(22),
	date_updated TIMESTAMP 
		DEFAULT CURRENT_TIMESTAMP
		ON UPDATE CURRENT_TIMESTAMP,
	date_created TIMESTAMP 
		DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(id),
	FOREIGN KEY (user_id) REFERENCES user (discord_id)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY (channel_id) REFERENCES channel (id)
		ON UPDATE CASCADE
		ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_group (
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(32),
	role_id CHAR(18),
	max_capacity INT DEFAULT 0,
	is_private TINYINT(1) DEFAULT 0,
	date_updated TIMESTAMP 
		DEFAULT CURRENT_TIMESTAMP
		ON UPDATE CURRENT_TIMESTAMP,
	date_created TIMESTAMP 
		DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(id)
);

/*CREATE TABLE IF NOT EXISTS group_moderator (
	id INT NOT NULL AUTO_INCREMENT,
	group_id INT,
	user_id CHAR(18),
	date_updated TIMESTAMP 
		DEFAULT CURRENT_TIMESTAMP
		ON UPDATE CURRENT_TIMESTAMP,
	date_created TIMESTAMP 
		DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(id),
	FOREIGN KEY (user_id) REFERENCES user (discord_id)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY (group_id) REFERENCES user_group (id)
		ON UPDATE CASCADE
		ON DELETE CASCADE
);*/
