package com.defectivemafia.bot.action;

public enum ActionType {
	Disconnect,
	Exception,
	Ready,
	Resumed,
	Reconnected,
	Shutdown,
	HttpRequest,
	Update,
	SelfUpdateAvatar,
	SelfUpdateDiscriminator,
	SelfUpdateEmail,
	SelfUpdateMFA,
	SelfUpdateMobile,
	SelfUpdateName,
	SelfUpdateNitro,
	SelfUpdatePhoneNumber,
	SelfUpdateVerified,
	UserUpdateGame,
	UserUpdateOnlineStatus,
	UserUpdateAvatar,
	UserUpdateDiscriminator,
	UserUpdateName,
	UserTyping,
	MessageDelete,
	MessageEmbed,
	MessageReceived,
	MessageUpdate,
	MessageReactionAdd,
	MessageReactionRemoveAll,
	MessageReactionRemove,
	PrivateMessageDelete,
	PrivateMessageEmbed,
	PrivateMessageReceived,
	PrivateMessageUpdate,
	PrivateMessageReactionAdd,
	PrivateMessageReactionRemove,
	GuildReady,
	GuildAvailable,
	GuildBan,
	GuildJoin,
	GuildLeave,
	GuildUnavailable,
	GuildUnban,
	GuildMessageDelete,
	GuildMessageEmbed,
	GuildMessageReceived,
	GuildMessageUpdate,
	GuildMessageReactionRemoveAll,
	GuildMessageReactionAdd,
	GuildMessageReactionRemove,
	GuildUpdateAfkChannel,
	GuildUpdateAfkTimeout,
	GuildUpdateExplicitContentLevel,
	GuildUpdateFeatures,
	GuildUpdateIcon,
	GuildUpdateMFALevel,
	GuildUpdateName,
	GuildUpdateNotificationLevel,
	GuildUpdateOwner,
	GuildUpdateRegion,
	GuildUpdateSplash,
	GuildUpdateSystemChannel,
	GuildUpdateVerificationLevel,
	GuildMemberJoin,
	GuildMemberLeave,
	GuildMemberNickChange,
	GuildMemberRoleAdd,
	GuildMemberRoleRemove,
	GuildVoiceDeafen,
	GuildVoiceGuildDeafen,
	GuildVoiceGuildMute,
	GuildVoiceJoin,
	GuildVoiceMute,
	GuildVoiceSelfDeafen,
	GuildVoiceSelfMute,
	GuildVoiceSuppress,
	GuildVoiceUpdate,
	GuildVoiceLeave,
	GuildVoiceMove,
	TextChannelCreate,
	TextChannelDelete,
	TextChannelUpdatePermissions,
	TextChannelUpdateName,
	TextChannelUpdateNSFW,
	TextChannelUpdateParent,
	TextChannelUpdatePosition,
	TextChannelUpdateTopic,
	VoiceChannelCreate,
	VoiceChannelDelete,
	VoiceChannelUpdatePermissions,
	VoiceChannelUpdateBitrate,
	VoiceChannelUpdateName,
	VoiceChannelUpdateParent,
	VoiceChannelUpdatePosition,
	VoiceChannelUpdateUserLimit,
	CategoryCreate,
	CategoryDelete,
	CategoryUpdatePermissions,
	CategoryUpdateName,
	CategoryUpdatePosition,
	PrivateChannelCreate,
	PrivateChannelDelete,
	RoleCreate,
	RoleDelete,
	RoleUpdateColor,
	RoleUpdateHoisted,
	RoleUpdateMentionable,
	RoleUpdateName,
	RoleUpdatePermissions,
	RoleUpdatePosition,
	EmoteAdded,
	EmoteRemoved,
	EmoteUpdateName,
	EmoteUpdateRoles,
	FriendAdded,
	FriendRequestSent,
	FriendRequestReceived,
	UserBlocked,
	FriendRemoved,
	FriendRequestCanceled,
	FriendRequestIgnored,
	UserUnblocked,
	GroupJoin,
	GroupLeave,
	GroupUserJoin,
	GroupUserLeave,
	GroupUpdateIcon,
	GroupUpdateName,
	GroupUpdateOwner,
	GroupMessageDelete,
	GroupMessageEmbed,
	GroupMessageReceived,
	GroupMessageUpdate,
	GroupMessageReactionRemoveAll,
	GroupMessageReactionAdd,
	GroupMessageReactionRemove,
	CallCreate,
	CallDelete,
	CallUpdateRegion,
	CallUpdateRingingUsers,
	CallVoiceJoin,
	CallVoiceLeave,
	CallVoiceSelfDeafen
}
