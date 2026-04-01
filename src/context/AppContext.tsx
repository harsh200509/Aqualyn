import React, { createContext, useContext, useState, ReactNode, useEffect } from 'react';
import { User, Chat, Message, Folder, ThemeSettings, Post, Collection } from '../types';

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
  createFolder: (name: string, chatIds?: string[]) => string;
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
  blockContact: (contactId: string) => void;
  reportContact: (contactId: string) => void;
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
  addContact: (name: string, phone: string) => void;
  startChatWithContact: (contactId: string) => void;
  createGroupChat: (name: string, members: string[], options?: { description?: string; adminOnly?: boolean; disappearingMessages?: boolean }) => void;
  posts: Post[];
  setPosts: React.Dispatch<React.SetStateAction<Post[]>>;
  addPost: (post: Partial<Post>) => void;
  likePost: (postId: string) => void;
  commentPost: (postId: string, text: string) => void;
  followUser: (userId: string) => void;
  unfollowUser: (userId: string) => void;
  acceptFollowRequest: (userId: string) => void;
  rejectFollowRequest: (userId: string) => void;
  notifications: Notification[];
  setNotifications: React.Dispatch<React.SetStateAction<Notification[]>>;
  globalUsers: User[];
  archivePost: (postId: string) => void;
  pinPost: (postId: string) => void;
  savePost: (postId: string) => void;
  createCollection: (name: string) => void;
  addPostToCollection: (postId: string, collectionId: string) => void;
  updateStorySettings: (settings: Partial<User['storySettings']>) => void;
  toggleCloseFriend: (userId: string) => void;
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
  username: 'alexrivero',
  role: 'Product Designer & Digital Nomad',
  email: 'alex.rivero@aqualyn.io',
  bio: 'Exploring the intersection of liquid UI and human connection. Building the future of Aqualyn from somewhere near the ocean. 🌊',
  avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=200&h=200',
  largeAvatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=800&h=800',
  following: ['u2'],
  followers: ['u3'],
  phone: '+1 555 123 4567'
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
    username: 'sarahj',
    role: 'Frontend Developer',
    email: 'sarah@aqualyn.io',
    bio: 'Coding the future.',
    avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=200&h=200',
    largeAvatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=800&h=800',
    phone: '+1 555 987 6543',
    isPrivate: true,
    followers: ['u1']
  },
  {
    id: 'u3',
    name: 'Marcus Chen',
    username: 'marcusc',
    role: 'Product Manager',
    email: 'marcus@aqualyn.io',
    bio: 'Organizing chaos.',
    avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=200&h=200',
    largeAvatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=800&h=800',
    phone: '+1 555 444 3333',
    following: ['u1']
  }
];

const globalUsers: User[] = [
  ...initialContacts,
  {
    id: 'u4',
    name: 'Elena Rodriguez',
    username: 'elenar',
    role: 'UX Researcher',
    email: 'elena@aqualyn.io',
    bio: 'Understanding users.',
    avatar: 'https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&q=80&w=200&h=200',
    largeAvatar: 'https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&q=80&w=800&h=800',
    phone: '+1 555 111 2222',
    isPrivate: true
  },
  {
    id: 'u5',
    name: 'David Kim',
    username: 'davidk',
    role: 'Backend Engineer',
    email: 'david@aqualyn.io',
    bio: 'Scaling systems.',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=200&h=200',
    largeAvatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=800&h=800',
    phone: '+1 555 999 8888'
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
  const [posts, setPosts] = useState<Post[]>([
    {
      id: 'p1',
      userId: 'u1',
      userName: 'Me',
      userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Me',
      imageUrl: 'https://images.unsplash.com/photo-1558655146-d09347e92766?auto=format&fit=crop&q=80&w=600&h=600',
      caption: 'Exploring the deep blue! 🌊 #Aqualyn #Underwater',
      likes: ['u2', 'u3'],
      comments: [
        { id: 'c1', userId: 'u2', userName: 'Sarah Jenkins', userAvatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=200&h=200', text: 'Wow, this looks amazing!', timestamp: '1h ago' }
      ],
      timestamp: '2h ago'
    },
    {
      id: 'p2',
      userId: 'u1',
      userName: 'Me',
      userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Me',
      imageUrl: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=600&h=600',
      caption: 'City lights and quiet nights. 🌃',
      likes: ['u3'],
      comments: [],
      timestamp: 'Yesterday'
    }
  ]);
  const [notifications, setNotifications] = useState<Notification[]>([]);
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

  const addContact = (name: string, phone: string) => {
    const newContact: User = {
      id: `u${Date.now()}`,
      name,
      username: name.toLowerCase().replace(/\s+/g, '_'),
      role: phone, // using role to store phone for now
      phone,
      email: '',
      bio: 'Hey there! I am using Aqualyn.',
      avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${name}`,
      largeAvatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${name}`
    };
    setContacts(prev => [...prev, newContact]);
    addToast('Contact added successfully!', 'success');
  };

  const startChatWithContact = (contactId: string) => {
    // Find if a 1-on-1 chat already exists with this contact
    const existingChat = chats.find(c => !c.isGroup && c.id === contactId);
    
    if (existingChat) {
      setActiveChatId(existingChat.id);
    } else {
      const contact = contacts.find(c => c.id === contactId);
      if (!contact) return;
      
      const newChat: Chat = {
        id: contact.id, // Use contact ID as chat ID for 1-on-1 for simplicity
        name: contact.name,
        avatar: contact.avatar,
        lastMessage: '',
        lastMessageTime: 'Just now',
        unreadCount: 0,
        isGroup: false,
        participantIds: [currentUser?.id || 'u1', contact.id]
      };
      
      setChats(prev => [newChat, ...prev]);
      setMessages(prev => ({ ...prev, [newChat.id]: [] }));
      setActiveChatId(newChat.id);
    }
  };

  const createGroupChat = (name: string, members: string[], options?: { description?: string; adminOnly?: boolean; disappearingMessages?: boolean }) => {
    const newChatId = `g${Date.now()}`;
    const newChat: Chat = {
      id: newChatId,
      name,
      avatar: `https://api.dicebear.com/7.x/initials/svg?seed=${name}`,
      lastMessage: 'Group created',
      lastMessageTime: 'Just now',
      unreadCount: 0,
      isGroup: true,
      participantIds: [currentUser?.id || 'me', ...members],
      description: options?.description,
      adminOnly: options?.adminOnly,
      disappearingMessages: options?.disappearingMessages,
    };

    setChats(prev => [newChat, ...prev]);
    setMessages(prev => ({
      ...prev,
      [newChatId]: [{
        id: `m${Date.now()}`,
        chatId: newChatId,
        text: `You created group "${name}"`,
        senderId: 'system',
        timestamp: 'Just now',
        isRead: true,
      }]
    }));
    setActiveChatId(newChatId);
    addToast('Group created successfully!', 'success');
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
  
  const blockContact = (contactId: string) => {
    setCurrentUser(prev => {
      if (!prev) return prev;
      const blocked = prev.blockedUsers || [];
      if (blocked.includes(contactId)) {
        return { ...prev, blockedUsers: blocked.filter(id => id !== contactId) };
      }
      return { ...prev, blockedUsers: [...blocked, contactId] };
    });
    const isBlocked = currentUser?.blockedUsers?.includes(contactId);
    addToast(isBlocked ? 'Contact unblocked' : 'Contact blocked', 'info');
  };

  const reportContact = (contactId: string) => {
    addToast('Contact reported to Aqualyn safety team', 'success');
  };

  const markAsRead = (chatId: string) => {
    setChats(prev => prev.map(c => c.id === chatId ? { ...c, unreadCount: 0 } : c));
  };

  const createFolder = (name: string, chatIds: string[] = []) => {
    const folderExists = folders.some(f => f.name.toLowerCase() === name.trim().toLowerCase());
    if (folderExists) {
      addToast('A folder with this name already exists', 'error');
      return '';
    }
    const newFolderId = `f${Date.now()}`;
    const newFolder: Folder = {
      id: newFolderId,
      name: name.trim(),
      chatIds
    };
    setFolders(prev => [...prev, newFolder]);
    addToast(`Folder "${name.trim()}" created`, 'success');
    return newFolderId;
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
    // Check if it's a 1-on-1 chat and if the target user is private
    const chat = chats.find(c => c.id === chatId);
    if (chat && !chat.isGroup) {
      const targetUserId = chat.participantIds?.find(id => id !== currentUser?.id);
      const targetUser = globalUsers.find(u => u.id === targetUserId);
      
      if (targetUser?.isPrivate && !currentUser?.following?.includes(targetUser.id)) {
        addToast(`You must be following ${targetUser.name} to send messages.`, 'error');
        return;
      }
    }

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

  const addPost = (post: Partial<Post>) => {
    if (!currentUser) return;
    const newPost: Post = {
      id: `p${Date.now()}`,
      userId: currentUser.id,
      userName: currentUser.name,
      userAvatar: currentUser.avatar,
      caption: post.caption || '',
      imageUrl: post.imageUrl,
      videoUrl: post.videoUrl,
      likes: [],
      comments: [],
      timestamp: 'Just now',
    };
    setPosts(prev => [newPost, ...prev]);
    addToast('Post created successfully', 'success');
  };

  const likePost = (postId: string) => {
    if (!currentUser) return;
    setPosts(prev => prev.map(post => {
      if (post.id === postId) {
        const isLiked = post.likes.includes(currentUser.id);
        const newLikes = isLiked 
          ? post.likes.filter(id => id !== currentUser.id)
          : [...post.likes, currentUser.id];
        
        if (!isLiked && post.userId !== currentUser.id) {
          setNotifications(n => [{
            id: `n${Date.now()}`,
            userId: post.userId,
            type: 'like',
            sourceUserId: currentUser.id,
            sourceUserName: currentUser.name,
            sourceUserAvatar: currentUser.avatar,
            targetId: postId,
            timestamp: 'Just now',
            read: false
          }, ...n]);
        }
        return { ...post, likes: newLikes };
      }
      return post;
    }));
  };

  const commentPost = (postId: string, text: string) => {
    if (!currentUser) return;
    setPosts(prev => prev.map(post => {
      if (post.id === postId) {
        if (post.userId !== currentUser.id) {
          setNotifications(n => [{
            id: `n${Date.now()}`,
            userId: post.userId,
            type: 'comment',
            sourceUserId: currentUser.id,
            sourceUserName: currentUser.name,
            sourceUserAvatar: currentUser.avatar,
            targetId: postId,
            text,
            timestamp: 'Just now',
            read: false
          }, ...n]);
        }
        return {
          ...post,
          comments: [...post.comments, {
            id: `c${Date.now()}`,
            userId: currentUser.id,
            userName: currentUser.name,
            userAvatar: currentUser.avatar,
            text,
            timestamp: 'Just now'
          }]
        };
      }
      return post;
    }));
  };

  const followUser = (userId: string) => {
    if (!currentUser) return;
    const targetUser = globalUsers.find(c => c.id === userId);
    if (!targetUser) return;

    if (targetUser.isPrivate) {
      // Send follow request
      setContacts(prev => prev.map(c => {
        if (c.id === userId) {
          return { ...c, followRequests: [...(c.followRequests || []), currentUser.id] };
        }
        return c;
      }));
      setNotifications(n => [{
        id: `n${Date.now()}`,
        userId: userId,
        type: 'follow_request',
        sourceUserId: currentUser.id,
        sourceUserName: currentUser.name,
        sourceUserAvatar: currentUser.avatar,
        timestamp: 'Just now',
        read: false
      }, ...n]);
      addToast('Follow request sent', 'success');
    } else {
      // Direct follow
      setCurrentUser({ ...currentUser, following: [...(currentUser.following || []), userId] });
      setContacts(prev => prev.map(c => {
        if (c.id === userId) {
          return { ...c, followers: [...(c.followers || []), currentUser.id] };
        }
        return c;
      }));
      setNotifications(n => [{
        id: `n${Date.now()}`,
        userId: userId,
        type: 'follow',
        sourceUserId: currentUser.id,
        sourceUserName: currentUser.name,
        sourceUserAvatar: currentUser.avatar,
        timestamp: 'Just now',
        read: false
      }, ...n]);
      addToast(`You are now following ${targetUser.name}`, 'success');
    }
  };

  const unfollowUser = (userId: string) => {
    if (!currentUser) return;
    setCurrentUser({ ...currentUser, following: (currentUser.following || []).filter(id => id !== userId) });
    setContacts(prev => prev.map(c => {
      if (c.id === userId) {
        return { ...c, followers: (c.followers || []).filter(id => id !== currentUser.id) };
      }
      return c;
    }));
  };

  const acceptFollowRequest = (userId: string) => {
    if (!currentUser) return;
    setCurrentUser({
      ...currentUser,
      followRequests: (currentUser.followRequests || []).filter(id => id !== userId),
      followers: [...(currentUser.followers || []), userId]
    });
    setContacts(prev => prev.map(c => {
      if (c.id === userId) {
        return { ...c, following: [...(c.following || []), currentUser.id] };
      }
      return c;
    }));
    setNotifications(n => n.filter(notif => notif.type === 'follow_request' && notif.sourceUserId === userId ? false : true));
    addToast('Follow request accepted', 'success');
  };

  const rejectFollowRequest = (userId: string) => {
    if (!currentUser) return;
    setCurrentUser({
      ...currentUser,
      followRequests: (currentUser.followRequests || []).filter(id => id !== userId)
    });
    setNotifications(n => n.filter(notif => notif.type === 'follow_request' && notif.sourceUserId === userId ? false : true));
  };

  const archivePost = (postId: string) => {
    setPosts(prev => prev.map(p => p.id === postId ? { ...p, isArchived: !p.isArchived } : p));
    addToast('Post archived', 'info');
  };

  const pinPost = (postId: string) => {
    setPosts(prev => prev.map(p => p.id === postId ? { ...p, isPinned: !p.isPinned } : p));
    addToast('Post pinned to profile', 'success');
  };

  const savePost = (postId: string) => {
    if (!currentUser) return;
    const isSaved = currentUser.savedPostIds?.includes(postId);
    const newSavedIds = isSaved 
      ? (currentUser.savedPostIds || []).filter(id => id !== postId)
      : [...(currentUser.savedPostIds || []), postId];
    
    setCurrentUser({ ...currentUser, savedPostIds: newSavedIds });
    addToast(isSaved ? 'Removed from saved' : 'Post saved', 'success');
  };

  const createCollection = (name: string) => {
    if (!currentUser) return;
    const newCollection: Collection = {
      id: `c${Date.now()}`,
      name,
      postIds: []
    };
    setCurrentUser({
      ...currentUser,
      collections: [...(currentUser.collections || []), newCollection]
    });
    addToast(`Collection "${name}" created`, 'success');
  };

  const addPostToCollection = (postId: string, collectionId: string) => {
    if (!currentUser) return;
    setCurrentUser({
      ...currentUser,
      collections: (currentUser.collections || []).map(c => 
        c.id === collectionId ? { ...c, postIds: [...c.postIds, postId] } : c
      )
    });
    addToast('Added to collection', 'success');
  };

  const updateStorySettings = (settings: Partial<User['storySettings']>) => {
    if (!currentUser) return;
    setCurrentUser({
      ...currentUser,
      storySettings: { ...(currentUser.storySettings || { hideStoryFrom: [], allowReplies: 'everyone' }), ...settings }
    });
  };

  const toggleCloseFriend = (userId: string) => {
    if (!currentUser) return;
    const isFriend = currentUser.closeFriends?.includes(userId);
    const newFriends = isFriend
      ? (currentUser.closeFriends || []).filter(id => id !== userId)
      : [...(currentUser.closeFriends || []), userId];
    
    setCurrentUser({ ...currentUser, closeFriends: newFriends });
  };

  return (
    <AppContext.Provider value={{ 
      currentUser, setCurrentUser, chats, setChats, messages, contacts, sendMessage, editMessage, deleteMessage, addReaction,
      activeChatId, setActiveChatId, activeContactId, setActiveContactId,
      toasts, addToast, removeToast, isLoading, setIsLoading,
      folders, setFolders, createFolder, deleteFolder, addChatToFolder, theme, setTheme,
      aquaIntensity, setAquaIntensity,
      archiveChat, pinChat, muteChat, deleteChat, clearHistory, blockContact, reportContact, markAsRead,
      appLockPin, setAppLockPin, archiveLockPin, setArchiveLockPin, isAppLocked, setIsAppLocked,
      stories, setStories, addStory, addStoryComment, addContact, startChatWithContact, createGroupChat,
      posts, setPosts, addPost, likePost, commentPost, followUser, unfollowUser, acceptFollowRequest, rejectFollowRequest, notifications, setNotifications, globalUsers,
      archivePost, pinPost, savePost, createCollection, addPostToCollection, updateStorySettings, toggleCloseFriend
    }}>
      {children}
    </AppContext.Provider>
  );
};

export function useAppContext() {
  const context = useContext(AppContext);
  if (!context) throw new Error('useAppContext must be used within AppProvider');
  return context;
}
