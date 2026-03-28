import React from 'react';
import { motion } from 'motion/react';
import { ArrowLeft, MessageCircle, Phone, Video, Info, Bell, Ban, Trash2, Lock, ShieldCheck } from 'lucide-react';
import { useAppContext } from '../context/AppContext';

export default function ContactProfileScreen({ onBack, onNavigate }: { onBack: () => void, onNavigate: (s: string) => void }) {
  const { contacts, activeContactId, setActiveChatId, addToast, chats, setChats } = useAppContext();
  const [requestSent, setRequestSent] = React.useState(false);
  
  const contact = contacts.find(c => c.id === activeContactId);

  if (!contact) return null;

  const handleMessage = () => {
    setActiveChatId(contact.id);
    onNavigate('chat-detail');
  };

  const handleRequestSecretChat = () => {
    setRequestSent(true);
    addToast('Secret chat request sent to ' + contact.name, 'info');
    
    // Mock acceptance after 2 seconds
    setTimeout(() => {
      const existingChat = chats.find(c => c.id === contact.id);
      if (existingChat) {
        setChats(prev => prev.map(c => c.id === contact.id ? { ...c, isSecret: true, selfDestructTimer: 60 } : c));
      } else {
        setChats(prev => [...prev, {
          id: contact.id,
          name: contact.name,
          avatar: contact.avatar,
          isSecret: true,
          selfDestructTimer: 60,
          lastMessage: 'Secret chat started',
          lastMessageTime: 'Just now'
        }]);
      }
      addToast(contact.name + ' accepted your secret chat request!', 'success');
      setRequestSent(false);
      setActiveChatId(contact.id);
      onNavigate('chat-detail');
    }, 2000);
  };

  return (
    <motion.div initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: 20 }} className="bg-surface min-h-screen pb-32">
      <header className="fixed top-0 w-full z-50 bg-slate-50/70 backdrop-blur-xl border-b border-white/15 shadow-[0_8px_32px_0_rgba(0,87,189,0.06)] h-16 flex items-center px-6">
        <button onClick={onBack} className="text-slate-500 hover:bg-white/20 p-2 rounded-full transition-colors active:scale-95 duration-200 mr-4">
          <ArrowLeft className="w-6 h-6" />
        </button>
        <h1 className="text-xl font-black text-on-surface font-headline tracking-tight">Contact Info</h1>
      </header>

      <main className="pt-24 px-4 max-w-2xl mx-auto space-y-6">
        <div className="flex flex-col items-center text-center space-y-4">
          <div className="w-32 h-32 rounded-full border-4 border-white shadow-xl overflow-hidden">
            <img src={contact.avatar} alt={contact.name} className="w-full h-full object-cover" />
          </div>
          <div>
            <h2 className="text-3xl font-extrabold font-headline text-on-surface">{contact.name}</h2>
          </div>
          
          <div className="flex gap-4 pt-2">
            <button onClick={handleMessage} className="flex flex-col items-center gap-2 p-4 rounded-2xl glass-card border border-white/40 shadow-sm hover:bg-white/60 transition-colors active:scale-95 w-24">
              <MessageCircle className="w-6 h-6 text-primary" />
              <span className="text-xs font-semibold text-on-surface">Message</span>
            </button>
            <button onClick={handleRequestSecretChat} disabled={requestSent} className={`flex flex-col items-center gap-2 p-4 rounded-2xl glass-card border border-white/40 shadow-sm hover:bg-white/60 transition-colors active:scale-95 w-24 ${requestSent ? 'opacity-50' : ''}`}>
              {requestSent ? <ShieldCheck className="w-6 h-6 text-amber-500 animate-pulse" /> : <Lock className="w-6 h-6 text-green-500" />}
              <span className="text-xs font-semibold text-on-surface">{requestSent ? 'Pending...' : 'Secret'}</span>
            </button>
            <button className="flex flex-col items-center gap-2 p-4 rounded-2xl glass-card border border-white/40 shadow-sm hover:bg-white/60 transition-colors active:scale-95 w-24">
              <Phone className="w-6 h-6 text-emerald-500" />
              <span className="text-xs font-semibold text-on-surface">Audio</span>
            </button>
          </div>
        </div>

        <div className="glass-card rounded-[2rem] overflow-hidden border border-white/40 shadow-sm mt-8">
          <div className="p-5 border-b border-white/20">
            <h3 className="text-sm font-bold text-primary uppercase tracking-wider mb-2">About</h3>
            <p className="text-on-surface">{contact.bio || 'Hey there! I am using Aqualyn.'}</p>
          </div>
          <div className="p-5 border-b border-white/20 flex items-center justify-between hover:bg-white/40 transition-colors cursor-pointer">
            <div className="flex items-center gap-4">
              <Bell className="w-5 h-5 text-on-surface-variant" />
              <span className="font-semibold text-on-surface">Mute Notifications</span>
            </div>
            <div className="w-12 h-6 rounded-full bg-surface-container-highest p-1 cursor-pointer">
              <div className="w-4 h-4 rounded-full bg-white shadow-sm"></div>
            </div>
          </div>
          <div className="p-5 border-b border-white/20 flex items-center justify-between hover:bg-white/40 transition-colors cursor-pointer">
            <div className="flex items-center gap-4">
              <Info className="w-5 h-5 text-on-surface-variant" />
              <span className="font-semibold text-on-surface">Media, Links, and Docs</span>
            </div>
            <span className="text-sm font-bold text-secondary">12</span>
          </div>
        </div>

        <div className="glass-card rounded-[2rem] overflow-hidden border border-white/40 shadow-sm">
          <div className="p-5 border-b border-white/20 flex items-center gap-4 hover:bg-red-50/50 transition-colors cursor-pointer text-red-500">
            <Ban className="w-5 h-5" />
            <span className="font-semibold">Block {contact.name}</span>
          </div>
          <div className="p-5 flex items-center gap-4 hover:bg-red-50/50 transition-colors cursor-pointer text-red-500">
            <Trash2 className="w-5 h-5" />
            <span className="font-semibold">Report Contact</span>
          </div>
        </div>
      </main>
    </motion.div>
  );
}
