import React from 'react';
import { motion } from 'motion/react';
import { ArrowLeft, Search, UserPlus } from 'lucide-react';
import { useAppContext } from '../context/AppContext';

export default function ContactsScreen({ onNavigate }: { onNavigate: (s: string) => void }) {
  const { contacts, setActiveContactId, setActiveChatId, isLoading } = useAppContext();

  const handleContactClick = (id: string) => {
    setActiveContactId(id);
    onNavigate('contact-profile');
  };

  const handleMessageClick = (e: React.MouseEvent, id: string) => {
    e.stopPropagation();
    // In a real app, this would find or create a chat with this user
    setActiveChatId(id); 
    onNavigate('chat-detail');
  };

  const SkeletonContact = () => (
    <div className="p-4 rounded-2xl flex items-center gap-4 animate-pulse">
      <div className="w-14 h-14 rounded-full bg-white/10 shrink-0"></div>
      <div className="flex-1 min-w-0 space-y-2 border-b border-surface-container pb-2">
        <div className="h-4 bg-white/10 rounded-full w-1/3"></div>
        <div className="h-3 bg-white/10 rounded-full w-1/2"></div>
      </div>
    </div>
  );

  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="min-h-screen bg-surface pb-28">
      <header className="fixed top-0 w-full z-50 bg-slate-50/70 backdrop-blur-xl border-b border-white/15 shadow-[0_8px_32px_0_rgba(0,87,189,0.06)]">
        <div className="flex items-center justify-between px-6 h-16 w-full max-w-none">
          <div className="flex items-center gap-4">
            <h1 className="text-2xl font-black text-on-surface font-headline tracking-tight">Contacts</h1>
          </div>
          <div className="flex items-center gap-4">
            <button className="p-2 rounded-full text-cyan-600 hover:bg-white/20 transition-colors active:scale-95 duration-200">
              <Search className="w-6 h-6" />
            </button>
            <button className="p-2 rounded-full text-cyan-600 hover:bg-white/20 transition-colors active:scale-95 duration-200">
              <UserPlus className="w-6 h-6" />
            </button>
          </div>
        </div>
      </header>

      <main className="pt-20 px-4 max-w-2xl mx-auto">
        {isLoading ? (
          <div className="space-y-2">
            {[1, 2, 3, 4, 5].map(i => <SkeletonContact key={i} />)}
          </div>
        ) : contacts.length === 0 ? (
          <div className="text-center mt-20 opacity-60">
            <p className="text-on-surface-variant font-medium">No contacts found.</p>
            <p className="text-sm mt-2">Add contacts to start chatting.</p>
          </div>
        ) : (
          <div className="space-y-2">
            {contacts.map(contact => (
              <div 
                key={contact.id} 
                onClick={() => handleContactClick(contact.id)} 
                className="p-4 rounded-2xl flex items-center gap-4 cursor-pointer hover:bg-white/40 transition-all border border-transparent hover:border-white/20"
              >
                <div className="w-14 h-14 rounded-full overflow-hidden shadow-sm">
                  <img src={contact.avatar} alt={contact.name} className="w-full h-full object-cover" />
                </div>
                <div className="flex-1 min-w-0 border-b border-surface-container pb-2">
                  <h3 className="font-headline font-semibold text-on-surface truncate">{contact.name}</h3>
                  <p className="text-sm text-on-surface-variant truncate">{contact.role}</p>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </motion.div>
  );
}
