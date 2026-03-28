export interface User {
  id: string;
  name: string;
  role: string;
  email: string;
  bio: string;
  avatar: string;
  largeAvatar: string;
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
