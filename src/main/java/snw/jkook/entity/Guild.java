package snw.jkook.entity;

import org.jetbrains.annotations.Nullable;
import snw.jkook.entity.abilities.AvatarHolder;
import snw.jkook.entity.abilities.InviteHolder;
import snw.jkook.entity.abilities.MasterHolder;
import snw.jkook.entity.abilities.Nameable;
import snw.jkook.entity.mute.MuteResult;

import java.util.Collection;

/**
 * Represents a Guild.
 */
public interface Guild extends Nameable, AvatarHolder, MasterHolder, InviteHolder {

    /**
     * Get the ID of this guild.
     */
    String getId();

    /**
     * Get the users in this guild.
     */
    Iterable<User> getUsers();

    /**
     * Get the online users in this guild.
     */
    Iterable<User> getOnlineUsers();

    /**
     * Get the voice server region of this guild.
     */
    String getVoiceChannelServerRegion();

    /**
     * Get the custom emojis in this guild.
     */
    Collection<CustomEmoji> getCustomEmojis();

    /**
     * Get the online user count.
     */
    int getOnlineUserCount();

    /**
     * Get the total user count.
     */
    int getUserCount();

    /**
     * Return true if this guild is public.
     */
    boolean isPublic();

    /**
     * Get the mute status of this guild.
     */
    MuteResult getMuteStatus();

    /**
     * Leave this guild. This <b>CANNOT</b> be undone!
     */
    void leave();

    /**
     * Ban the user from this guild.
     *
     * @param user The user to be banned
     * @param reason The reason
     * @param delMessageDays The value passed in determines how many days the message sent by this user is deleted
     */
    void ban(User user, @Nullable String reason, int delMessageDays);

    /**
     * Unban the user from this guild.
     *
     * @param user The user to be unbanned
     */
    void unban(User user);

    /**
     * Get the users banned by this guild.
     */
    Collection<User> getBannedUsers();

    NotifyType getNotifyType();

    /**
     * Represents notify type.
     */
    enum NotifyType {

        /**
         * Use the settings defined by the guild.
         */
        DEFAULT(0),

        /**
         * Always notify.
         */
        ALL(1),

        /**
         * Notify on be mentioned only.
         */
        MENTION_ONLY(2),

        /**
         * Never notify.
         */
        NO_NOTIFY(3);

        private final int value;

        NotifyType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
