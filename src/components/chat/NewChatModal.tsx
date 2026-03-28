import React, { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { X, Search, Users, ArrowRight } from 'lucide-react';
import { useAppContext } from '../../context/AppContext';

interface NewChatModalProps {
  isOpen: boolean;
  onClose: () => void;
  onNavigate: (s: string) => void;
}

export default function NewChatModal({ isOpen, onClose, onNavigate }: NewChatModalProps) {
  const { contacts, setActiveChatId } = useAppContext();
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedContacts, setSelectedContacts] = useState<string[]>([]);

  const filteredContacts = contacts.filter(c => 
    c.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const toggleContact = (id: string) => {
    setSelectedContacts(prev => 
      prev.includes(id) ? prev.filter(cId => cId !== id) : [...prev, id]
    );
  };

  const handleStartChat = () => {
    if (selectedContacts.length === 0) return;
    
    // In a real app, we would create a new chat in the backend here
    // For now, just navigate to the chat detail of the first selected contact
    // or a mock group chat
    setActiveChatId(selectedContacts[0]);
    onClose();
    onNavigate('chat-detail');
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <>
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="fixed inset-0 bg-black/40 backdrop-blur-sm z-[100]"
          />
          <motion.div
            initial={{ y: '100%' }}
            animate={{ y: 0 }}
            exit={{ y: '100%' }}
            transition={{ type: 'spring', damping: 25, stiffness: 200 }}
            className="fixed inset-x-0 bottom-0 z-[101] bg-surface rounded-t-[2rem] shadow-2xl h-[85vh] flex flex-col overflow-hidden border-t border-white/20"
          >
            <div className="flex items-center justify-between p-6 border-b border-white/10">
              <h2 className="text-xl font-bold font-headline text-on-surface">New Chat</h2>
              <button onClick={onClose} className="p-2 rounded-full hover:bg-white/10 transition-colors">
                <X className="w-6 h-6 text-on-surface-variant" />
              </button>
            </div>

            <div className="p-4 border-b border-white/10">
              <div className="relative">
                <input
                  type="text"
                  placeholder="Search contacts..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full h-12 bg-white/5 border border-white/10 rounded-xl pl-12 pr-4 text-on-surface placeholder:text-on-surface-variant/50 focus:outline-none focus:ring-2 focus:ring-secondary/50 transition-all"
                />
                <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-on-surface-variant" />
              </div>
            </div>

            <div className="flex-1 overflow-y-auto p-2">
              <div 
                className="p-4 flex items-center gap-4 cursor-pointer hover:bg-white/5 rounded-xl transition-colors mb-2"
                onClick={() => {}}
              >
                <div className="w-12 h-12 rounded-full bg-secondary/20 flex items-center justify-center text-secondary">
                  <Users className="w-6 h-6" />
                </div>
                <div className="flex-1">
                  <h3 className="font-semibold text-on-surface">New Group</h3>
                  <p className="text-sm text-on-surface-variant">Create a chat with multiple people</p>
                </div>
              </div>

              <div className="px-4 py-2 text-xs font-bold text-on-surface-variant uppercase tracking-wider">
                Contacts
              </div>

              {filteredContacts.length === 0 ? (
                <div className="text-center p-8 opacity-50">
                  <p className="text-on-surface-variant">No contacts found</p>
                </div>
              ) : (
                filteredContacts.map(contact => (
                  <div 
                    key={contact.id}
                    onClick={() => toggleContact(contact.id)}
                    className="p-4 flex items-center gap-4 cursor-pointer hover:bg-white/5 rounded-xl transition-colors"
                  >
                    <div className="relative">
                      <div className="w-12 h-12 rounded-full overflow-hidden">
                        <img src={contact.avatar} alt={contact.name} className="w-full h-full object-cover" />
                      </div>
                      {selectedContacts.includes(contact.id) && (
                        <div className="absolute -bottom-1 -right-1 w-5 h-5 bg-secondary rounded-full border-2 border-surface flex items-center justify-center">
                          <div className="w-2 h-2 bg-white rounded-full" />
                        </div>
                      )}
                    </div>
                    <div className="flex-1 min-w-0">
                      <h3 className="font-semibold text-on-surface truncate">{contact.name}</h3>
                      <p className="text-sm text-on-surface-variant truncate">{contact.role}</p>
                    </div>
                  </div>
                ))
              )}
            </div>

            {selectedContacts.length > 0 && (
              <div className="p-4 border-t border-white/10 bg-surface/80 backdrop-blur-md">
                <button 
                  onClick={handleStartChat}
                  className="w-full h-14 bg-gradient-to-r from-secondary to-primary-container text-white font-bold rounded-xl shadow-lg shadow-secondary/20 flex items-center justify-center gap-2 hover:scale-[1.02] active:scale-[0.98] transition-all"
                >
                  Start Chat {selectedContacts.length > 1 ? `(${selectedContacts.length})` : ''}
                  <ArrowRight className="w-5 h-5" />
                </button>
              </div>
            )}
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
}
