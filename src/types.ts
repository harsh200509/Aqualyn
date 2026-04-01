export interface Post {
  id: string;
  userId: string;
  userName: string;
  userAvatar: string;
  imageUrl?: string;
  videoUrl?: string;
  mediaUrl?: string;
  mediaType?: 'image' | 'video';
  caption: string;
  likes: string[];
  comments: { id: string; userId: string; userName: string; userAvatar: string; text: string; timestamp: string }[];
  timestamp: string;
  isArchived?: boolean;
  isPinned?: boolean;
}

export interface Collection {
  id: string;
  name: string;
  postIds: string[];
  coverImageUrl?: string;
}

export interface User {
  id: string;
  name: string;
  username: string;
  role: string;
  email: string;
  bio: string;
  avatar: string;
  largeAvatar: string;
  isPrivate?: boolean;
  followers?: string[];
  following?: string[];
  followRequests?: string[];
  blockedUsers?: string[];
  reportedUsers?: string[];
  phone?: string;
  savedPostIds?: string[];
  collections?: Collection[];
  closeFriends?: string[];
  storySettings?: {
    hideStoryFrom?: string[];
    allowReplies?: 'everyone' | 'following' | 'off';
  };
}

export interface Story {
  id: string;
  userId: string;
  userName: string;
  userAvatar: string;
  mediaUrl: string;
  mediaType: 'image' | 'video';
  timestamp: string;
  expiresAt: string;
  views: number;
  reactions: Record<string, number>;
  isCloseFriends?: boolean;
  stickers?: any[];
  viewers?: string[];
}

export interface Notification {
  id: string;
  userId: string;
  type: 'like' | 'comment' | 'follow' | 'follow_request' | 'story_like';
  sourceUserId: string;
  sourceUserName: string;
  sourceUserAvatar: string;
  targetId?: string; // postId, storyId, etc.
  text?: string;
  timestamp: string;
  read: boolean;
}

export interface Message {
  id: string;
  chatId: string;
  senderId: string;
  text?: string;
  imageUrl?: string;
  audioUrl?: string;
  videoUrl?: string;
  document?: { url: string; name: string; size: string };
  location?: { lat: number; lng: number; address: string };
  contact?: { name: string; phone: string; avatar?: string };
  replyToId?: string;
  reactions?: Record<string, string[]>;
  timestamp: string;
  isRead: boolean;
  isEdited?: boolean;
  payment?: { amount: number; currency: string; status: 'completed' | 'pending' };
}

export interface Chat {
  id: string;
  name: string;
  isGroup?: boolean;
  isSecret?: boolean;
  selfDestructTimer?: number;
  avatar?: string;
  lastMessage?: string;
  lastMessageTime?: string;
  unreadCount?: number;
  isPinned?: boolean;
  isMuted?: boolean;
  isArchived?: boolean;
  isSystem?: boolean;
  isVoice?: boolean;
  participantIds?: string[];
  description?: string;
  adminOnly?: boolean;
  disappearingMessages?: boolean;
}

export interface Folder {
  id: string;
  name: string;
  chatIds: string[];
  icon?: string;
}

export interface ThemeSettings {
  mode: 'light' | 'dark';
  accentColor: string;
  wallpaper?: string;
  bubbleStyle: 'rounded' | 'sharp' | 'glass';
  fontSize: number;
}
