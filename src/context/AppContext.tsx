import React, { createContext, useContext, useState, ReactNode, useEffect } from 'react';
import { User, Chat, Message, Folder, ThemeSettings } from '../types';

export type ToastType = 'success' | 'error' | 'info';
export interface Toast {
  id: string;
  message: string;
  type: ToastType;
}

interface AppContextType {
  currentUser: User | null;
  setCurrentUser: (user: User | null) => void;
  chats: Chat[];
  setChats: React.Dispatch<React.SetStateAction<Chat[]>>;
  messages: Record<string, Message[]>;
  contacts: User[];
  sendMessage: (chatId: string, text: string, options?: Partial<Message>) => void;
  editMessage: (chatId: string, messageId: string, newText: string) => void;
  deleteMessage: (chatId: string, messageId: string) => void;
  addReaction: (chatId: string, messageId: string, emoji: string) => void;
  activeChatId: string | null;
  setActiveChatId: (id: string | null) => void;
  activeContactId: string | null;
  setActiveContactId: (id: string | null) => void;
  toasts: Toast[];
  addToast: (message: string, type: ToastType) => void;
  removeToast: (id: string) => void;
  isLoading: boolean;
  setIsLoading: (loading: boolean) => void;
  folders: Folder[];
  setFolders: React.Dispatch<React.SetStateAction<Folder[]>>;
  createFolder: (name: string) => void;
  deleteFolder: (folderId: string) => void;
  addChatToFolder: (chatId: string, folderId: string) => void;
  theme: ThemeSettings;
  setTheme: React.Dispatch<React.SetStateAction<ThemeSettings>>;
  aquaIntensity: number;
  setAquaIntensity: (val: number) => void;
  archiveChat: (chatId: string) => void;
  pinChat: (chatId: string) => void;
  muteChat: (chatId: string) => void;
  deleteChat: (chatId: string) => void;
  clearHistory: (chatId: string) => void;
  markAsRead: (chatId: string) => void;
  appLockPin: string | null;
  setAppLockPin: (pin: string | null) => void;
  archiveLockPin: string | null;
  setArchiveLockPin: (pin: string | null) => void;
  isAppLocked: boolean;
  setIsAppLocked: (locked: boolean) => void;
  stories: Story[];
  setStories: React.Dispatch<React.SetStateAction<Story[]>>;
  addStory: (story: Partial<Story>) => void;
  addStoryComment: (storyId: string, text: string) => void;
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
  music?: { title: string; artist: string; url: string };
}

export interface StoryComment {
  id: string;
  storyId: string;
  userId: string;
  userName: string;
  userAvatar: string;
  text: string;
  timestamp: string;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

const defaultUser: User = {
  id: 'u1',
  name: 'Alex Rivero',
  role: 'Product Designer & Digital Nomad',
  email: 'alex.rivero@aqualyn.io',
  bio: 'Exploring the intersection of liquid UI and human connection. Building the future of Aqualyn from somewhere near the ocean. 🌊',
  avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=200&h=200',
  largeAvatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=800&h=800'
};

const initialChats: Chat[] = [
  {
    id: 'c1',
    name: 'Design Team',
    isGroup: true,
    avatar: 'https://images.unsplash.com/photo-1522071820081-009f0129c71c?auto=format&fit=crop&q=80&w=200&h=200',
    lastMessage: 'Check out the new location!',
    lastMessageTime: '10:42 AM',
    unreadCount: 3,
    isPinned: true
  },
  {
    id: 'u2',
    name: 'Sarah Jenkins',
    avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=200&h=200',
    lastMessage: 'Sent a voice message',
    lastMessageTime: 'Yesterday',
    isVoice: true
  },
  {
    id: 'u3',
    name: 'Marcus Chen',
    isSecret: true,
    selfDestructTimer: 60,
    avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=200&h=200',
    lastMessage: 'This message will self-destruct',
    lastMessageTime: 'Monday',
  }
];

const initialMessages: Record<string, Message[]> = {
  'c1': [
    {
      id: 'm1',
      chatId: 'c1',
      senderId: 'u2',
      text: 'Hey everyone, check out this new design file!',
      document: { url: '#', name: 'Q3_Design_System.fig', size: '4.2 MB' },
      timestamp: '10:30 AM',
      isRead: true,
      reactions: { '👍': ['u1', 'u3'] }
    },
    {
      id: 'm2',
      chatId: 'c1',
      senderId: 'u1',
      text: 'Looks great! Where are we meeting to discuss this?',
      replyToId: 'm1',
      timestamp: '10:35 AM',
      isRead: true
    },
    {
      id: 'm3',
      chatId: 'c1',
      senderId: 'u3',
      text: 'I found a great coffee shop for our meeting.',
      location: { lat: 37.7749, lng: -122.4194, address: 'Blue Bottle Coffee, 115 Sansome St' },
      timestamp: '10:40 AM',
      isRead: true
    },
    {
      id: 'm4',
      chatId: 'c1',
      senderId: 'u2',
      audioUrl: 'mock-audio',
      timestamp: '10:42 AM',
      isRead: false
    }
  ],
  'u3': [
    {
      id: 'm5',
      chatId: 'u3',
      senderId: 'u3',
      text: 'This is a secret chat. Messages are end-to-end encrypted and cannot be forwarded.',
      timestamp: '10:00 AM',
      isRead: true
    }
  ]
};

const initialContacts: User[] = [
  {
    id: 'u2',
    name: 'Sarah Jenkins',
    role: 'Frontend Developer',
    email: 'sarah@aqualyn.io',
    bio: 'Coding the future.',
    avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=200&h=200',
    largeAvatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=800&h=800'
  },
  {
    id: 'u3',
    name: 'Marcus Chen',
    role: 'Product Manager',
    email: 'marcus@aqualyn.io',
    bio: 'Organizing chaos.',
    avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=200&h=200',
    largeAvatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=800&h=800'
  }
];

export function AppProvider({ children }: { children: ReactNode }) {
  const [currentUser, setCurrentUser] = useState<User | null>(defaultUser);
  const [chats, setChats] = useState<Chat[]>(initialChats);
  const [messages, setMessages] = useState<Record<string, Message[]>>(initialMessages);
  const [contacts, setContacts] = useState<User[]>(initialContacts);
  const [activeChatId, setActiveChatId] = useState<string | null>(null);
  const [activeContactId, setActiveContactId] = useState<string | null>(null);
  const [toasts, setToasts] = useState<Toast[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [folders, setFolders] = useState<Folder[]>([
    { id: 'f1', name: 'Work', chatIds: ['c1'] },
    { id: 'f2', name: 'Personal', chatIds: ['u2', 'u3'] }
  ]);
  const [theme, setTheme] = useState<ThemeSettings>({
    mode: 'light',
    accentColor: '#0891b2',
    bubbleStyle: 'rounded',
    fontSize: 16
  });
  const [aquaIntensity, setAquaIntensity] = useState(75);
  const [appLockPin, setAppLockPin] = useState<string | null>(null);
  const [archiveLockPin, setArchiveLockPin] = useState<string | null>(null);
  const [isAppLocked, setIsAppLocked] = useState(false);
  const [stories, setStories] = useState<Story[]>([
    {
      id: 's1',
      userId: 'u2',
      userName: 'Sarah Jenkins',
      userAvatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=200&h=200',
      mediaUrl: 'https://images.unsplash.com/photo-1558655146-d09347e92766?auto=format&fit=crop&q=80&w=600&h=900',
      mediaType: 'image',
      timestamp: '2h ago',
      expiresAt: '22h left',
      views: 124,
      reactions: { '❤️': 12, '🔥': 8 }
    },
    {
      id: 's2',
      userId: 'u3',
      userName: 'Marcus Chen',
      userAvatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=200&h=200',
      mediaUrl: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=600&h=900',
      mediaType: 'image',
      timestamp: '5h ago',
      expiresAt: '19h left',
      views: 89,
      reactions: { '😮': 4 }
    }
  ]);

  const addStory = (story: Partial<Story>) => {
    const newStory: Story = {
      id: `s${Date.now()}`,
      userId: currentUser?.id || 'u1',
      userName: currentUser?.name || 'Me',
      userAvatar: currentUser?.avatar || '',
      mediaUrl: story.mediaUrl || '',
      mediaType: story.mediaType || 'image',
      timestamp: 'Just now',
      expiresAt: '24h left',
      views: 0,
      reactions: {},
      ...story
    };
    setStories(prev => [newStory, ...prev]);
    addToast('Story posted!', 'success');
  };

  const addStoryComment = (storyId: string, text: string) => {
    addToast('Comment sent!', 'success');
    // In a real app, we'd add this to a separate state or backend
  };

  useEffect(() => {
    // Simulate initial loading
    const timer = setTimeout(() => setIsLoading(false), 1500);
    return () => clearTimeout(timer);
  }, []);

  const archiveChat = (chatId: string) => {
    setChats(prev => prev.map(c => c.id === chatId ? { ...c, isArchived: !c.isArchived } : c));
    addToast('Chat archived', 'info');
  };

  const pinChat = (chatId: string) => {
    setChats(prev => prev.map(c => c.id === chatId ? { ...c, isPinned: !c.isPinned } : c));
  };

  const muteChat = (chatId: string) => {
    setChats(prev => prev.map(c => c.id === chatId ? { ...c, isMuted: !c.isMuted } : c));
  };

  const deleteChat = (chatId: string) => {
    setChats(prev => prev.filter(c => c.id !== chatId));
    addToast('Chat deleted', 'info');
  };

  const clearHistory = (chatId: string) => {
    setMessages(prev => ({ ...prev, [chatId]: [] }));
    addToast('History cleared', 'info');
  };

  const markAsRead = (chatId: string) => {
    setChats(prev => prev.map(c => c.id === chatId ? { ...c, unreadCount: 0 } : c));
  };

  const createFolder = (name: string) => {
    const newFolder: Folder = {
      id: `f${Date.now()}`,
      name,
      chatIds: []
    };
    setFolders(prev => [...prev, newFolder]);
    addToast(`Folder "${name}" created`, 'success');
  };

  const deleteFolder = (folderId: string) => {
    setFolders(prev => prev.filter(f => f.id !== folderId));
    addToast('Folder deleted', 'info');
  };

  const addChatToFolder = (chatId: string, folderId: string) => {
    setFolders(prev => prev.map(f => {
      if (f.id === folderId) {
        if (f.chatIds.includes(chatId)) return f;
        return { ...f, chatIds: [...f.chatIds, chatId] };
      }
      return f;
    }));
    const folderName = folders.find(f => f.id === folderId)?.name;
    addToast(`Added to ${folderName}`, 'success');
  };

  const addToast = (message: string, type: ToastType) => {
    const id = Date.now().toString();
    setToasts(prev => [...prev, { id, message, type }]);
    setTimeout(() => removeToast(id), 3000);
  };

  const removeToast = (id: string) => {
    setToasts(prev => prev.filter(t => t.id !== id));
  };

  const sendMessage = (chatId: string, text: string, options?: Partial<Message>) => {
    const newMessage: Message = {
      id: Date.now().toString(),
      chatId,
      senderId: currentUser?.id || 'u1',
      text,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      isRead: false,
      ...options
    };
    
    setMessages(prev => ({
      ...prev,
      [chatId]: [...(prev[chatId] || []), newMessage]
    }));
  };

  const editMessage = (chatId: string, messageId: string, newText: string) => {
    setMessages(prev => ({
      ...prev,
      [chatId]: prev[chatId]?.map(m => m.id === messageId ? { ...m, text: newText, isEdited: true } : m) || []
    }));
  };

  const deleteMessage = (chatId: string, messageId: string) => {
    setMessages(prev => ({
      ...prev,
      [chatId]: prev[chatId]?.filter(m => m.id !== messageId) || []
    }));
  };

  const addReaction = (chatId: string, messageId: string, emoji: string) => {
    setMessages(prev => {
      const chatMsgs = prev[chatId] || [];
      const updatedMsgs = chatMsgs.map(msg => {
        if (msg.id === messageId) {
          const reactions = { ...(msg.reactions || {}) };
          const userReactions = reactions[emoji] || [];
          const userId = currentUser?.id || 'u1';
          
          if (userReactions.includes(userId)) {
            reactions[emoji] = userReactions.filter(id => id !== userId);
            if (reactions[emoji].length === 0) delete reactions[emoji];
          } else {
            reactions[emoji] = [...userReactions, userId];
          }
          
          return { ...msg, reactions };
        }
        return msg;
      });
      return { ...prev, [chatId]: updatedMsgs };
    });
  };

  return (
    <AppContext.Provider value={{ 
      currentUser, setCurrentUser, chats, setChats, messages, contacts, sendMessage, editMessage, deleteMessage, addReaction,
      activeChatId, setActiveChatId, activeContactId, setActiveContactId,
      toasts, addToast, removeToast, isLoading, setIsLoading,
      folders, setFolders, createFolder, deleteFolder, addChatToFolder, theme, setTheme,
      aquaIntensity, setAquaIntensity,
      archiveChat, pinChat, muteChat, deleteChat, clearHistory, markAsRead,
      appLockPin, setAppLockPin, archiveLockPin, setArchiveLockPin, isAppLocked, setIsAppLocked,
      stories, setStories, addStory, addStoryComment
    }}>
      {children}
    </AppContext.Provider>
  );
}

export function useAppContext() {
  const context = useContext(AppContext);
  if (!context) throw new Error('useAppContext must be used within AppProvider');
  return context;
}
