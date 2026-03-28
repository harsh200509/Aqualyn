import React, { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Play, Pause, FileText, Download, MapPin, CheckCheck, Reply, Copy, Trash2, Smile, Timer, Edit2, Wallet } from 'lucide-react';
import { Message } from '../../types';
import { useAppContext } from '../../context/AppContext';

interface MessageBubbleProps {
  msg: Message;
  isMe: boolean;
  onReply: (msg: Message) => void;
  onEdit?: (msg: Message) => void;
  replyMessage?: Message;
  onMediaClick?: (msg: Message) => void;
  isSecret?: boolean;
}

export default function MessageBubble({ msg, isMe, onReply, onEdit, replyMessage, onMediaClick, isSecret }: MessageBubbleProps) {
  const { deleteMessage, addReaction, currentUser, addToast } = useAppContext();
  const [showContextMenu, setShowContextMenu] = useState(false);
  const [isPlaying, setIsPlaying] = useState(false);
  const [audioProgress, setAudioProgress] = useState(0);
  const pressTimer = useRef<NodeJS.Timeout | null>(null);

  const handleTouchStart = () => {
    pressTimer.current = setTimeout(() => {
      setShowContextMenu(true);
    }, 500);
  };

  const handleTouchEnd = () => {
    if (pressTimer.current) clearTimeout(pressTimer.current);
  };

  // Mock audio playback
  useEffect(() => {
    let interval: NodeJS.Timeout;
    if (isPlaying) {
      interval = setInterval(() => {
        setAudioProgress(prev => {
          if (prev >= 100) {
            setIsPlaying(false);
            return 0;
          }
          return prev + 2;
        });
      }, 100);
    }
    return () => clearInterval(interval);
  }, [isPlaying]);

  const handleCopy = () => {
    if (msg.text) navigator.clipboard.writeText(msg.text);
    setShowContextMenu(false);
    addToast('Message copied', 'success');
  };

  const handleDelete = (forEveryone: boolean) => {
    deleteMessage(msg.chatId, msg.id);
    setShowContextMenu(false);
    addToast(forEveryone ? 'Message deleted for everyone' : 'Message deleted for you', 'success');
  };

  const handleReact = (emoji: string) => {
    addReaction(msg.chatId, msg.id, emoji);
    setShowContextMenu(false);
  };

  const emojis = ['👍', '❤️', '😂', '😮', '😢', '🙏'];

  return (
    <motion.div 
      layout
      initial={{ opacity: 0, y: 20, scale: 0.95 }}
      animate={{ opacity: 1, y: 0, scale: 1 }}
      transition={{ type: 'spring', damping: 25, stiffness: 300 }}
      className={`flex flex-col space-y-1 max-w-[85%] relative ${isMe ? 'items-end ml-auto' : 'items-start'}`}
    >
      
      {/* Context Menu Overlay */}
      <AnimatePresence>
        {showContextMenu && (
          <>
            <motion.div 
              initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
              className="fixed inset-0 z-40" 
              onClick={() => setShowContextMenu(false)} 
            />
            <motion.div 
              initial={{ opacity: 0, scale: 0.9, y: 10 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.9, y: 10 }}
              className={`absolute z-50 bottom-full mb-2 ${isMe ? 'right-0' : 'left-0'} glass-card rounded-2xl p-2 shadow-xl border border-white/20 flex flex-col gap-2 min-w-[200px]`}
            >
              <div className="flex justify-between px-2 py-1 border-b border-white/10 pb-2">
                {emojis.map(emoji => (
                  <button key={emoji} onClick={() => handleReact(emoji)} className="hover:scale-125 transition-transform text-xl">
                    {emoji}
                  </button>
                ))}
              </div>
              <button onClick={() => { onReply(msg); setShowContextMenu(false); }} className="flex items-center gap-3 px-3 py-2 hover:bg-white/10 rounded-xl text-sm font-medium text-on-surface transition-colors">
                <Reply className="w-4 h-4" /> Reply
              </button>
              {isMe && msg.text && onEdit && (
                <button onClick={() => { onEdit(msg); setShowContextMenu(false); }} className="flex items-center gap-3 px-3 py-2 hover:bg-white/10 rounded-xl text-sm font-medium text-on-surface transition-colors">
                  <Edit2 className="w-4 h-4" /> Edit
                </button>
              )}
              {msg.text && (
                <button onClick={handleCopy} className="flex items-center gap-3 px-3 py-2 hover:bg-white/10 rounded-xl text-sm font-medium text-on-surface transition-colors">
                  <Copy className="w-4 h-4" /> Copy
                </button>
              )}
              <button onClick={() => handleDelete(false)} className="flex items-center gap-3 px-3 py-2 hover:bg-red-500/20 text-red-500 rounded-xl text-sm font-medium transition-colors">
                <Trash2 className="w-4 h-4" /> Delete for me
              </button>
              {isMe && (
                <button onClick={() => handleDelete(true)} className="flex items-center gap-3 px-3 py-2 hover:bg-red-500/20 text-red-500 rounded-xl text-sm font-medium transition-colors">
                  <Trash2 className="w-4 h-4" /> Delete for everyone
                </button>
              )}
            </motion.div>
          </>
        )}
      </AnimatePresence>

      <div 
        onMouseDown={handleTouchStart}
        onMouseUp={handleTouchEnd}
        onMouseLeave={handleTouchEnd}
        onTouchStart={handleTouchStart}
        onTouchEnd={handleTouchEnd}
        onContextMenu={(e) => { e.preventDefault(); setShowContextMenu(true); }}
        className={`${isMe ? 'glass-sent rounded-tr-none' : 'glass-received rounded-tl-none'} rounded-2xl p-1 shadow-sm relative cursor-pointer group`}
      >
        {/* Reply Block */}
        {replyMessage && (
          <div className={`mx-2 mt-2 mb-1 p-2 rounded-xl text-sm border-l-4 ${isMe ? 'bg-white/10 border-white/50' : 'bg-black/5 border-secondary'}`}>
            <p className="font-bold text-xs opacity-70 mb-1">{replyMessage.senderId === currentUser?.id ? 'You' : 'Someone'}</p>
            <p className="truncate opacity-90">{replyMessage.text || 'Attachment'}</p>
          </div>
        )}

        {/* Media Content */}
        <div className="px-3 py-2">
          {msg.imageUrl && (
            <img 
              src={msg.imageUrl} 
              alt="Attachment" 
              className="w-full object-cover aspect-video rounded-xl mb-2 cursor-pointer" 
              onClick={(e) => { e.stopPropagation(); onMediaClick?.(msg); }}
              referrerPolicy="no-referrer"
            />
          )}
          
          {msg.videoUrl && (
            <div 
              className="relative w-full aspect-video rounded-xl overflow-hidden mb-2 bg-black/20 flex items-center justify-center cursor-pointer"
              onClick={(e) => { e.stopPropagation(); onMediaClick?.(msg); }}
            >
              <Play className="w-10 h-10 text-white opacity-80" />
            </div>
          )}

          {msg.audioUrl && (
            <div className="flex items-center gap-3 min-w-[200px] mb-1">
              <button 
                onClick={() => setIsPlaying(!isPlaying)}
                className={`w-10 h-10 rounded-full flex items-center justify-center shrink-0 ${isMe ? 'bg-white text-secondary' : 'bg-secondary text-white'}`}
              >
                {isPlaying ? <Pause className="w-5 h-5 fill-current" /> : <Play className="w-5 h-5 fill-current ml-1" />}
              </button>
              <div className="flex-1 h-1.5 bg-black/10 rounded-full overflow-hidden relative">
                <div className={`absolute left-0 top-0 bottom-0 ${isMe ? 'bg-white' : 'bg-secondary'}`} style={{ width: `${audioProgress}%` }} />
              </div>
              <span className="text-xs font-mono opacity-70">0:14</span>
            </div>
          )}

          {msg.document && (
            <div className={`flex items-center gap-3 p-3 rounded-xl mb-1 ${isMe ? 'bg-white/10' : 'bg-black/5'}`}>
              <div className={`p-2 rounded-lg ${isMe ? 'bg-white/20' : 'bg-secondary/20 text-secondary'}`}>
                <FileText className="w-6 h-6" />
              </div>
              <div className="flex-1 min-w-0">
                <p className="font-semibold text-sm truncate">{msg.document.name}</p>
                <p className="text-xs opacity-70">{msg.document.size}</p>
              </div>
              <button className={`p-2 rounded-full hover:bg-black/10 transition-colors`}>
                <Download className="w-5 h-5" />
              </button>
            </div>
          )}

          {msg.location && (
            <div className="mb-2">
              <div className="w-full h-32 bg-black/10 rounded-xl mb-2 relative overflow-hidden flex items-center justify-center">
                <MapPin className="w-8 h-8 text-red-500 absolute z-10" />
                {/* Mock map background */}
                <div className="absolute inset-0 opacity-30" style={{ backgroundImage: 'radial-gradient(circle at center, transparent 0%, rgba(0,0,0,0.1) 100%), repeating-linear-gradient(45deg, rgba(0,0,0,0.05) 0px, rgba(0,0,0,0.05) 10px, transparent 10px, transparent 20px)' }} />
              </div>
              <p className="text-sm font-medium leading-tight">{msg.location.address}</p>
            </div>
          )}

          {/* Payment Content */}
          {msg.payment && (
            <div className={`flex items-center gap-3 p-3 rounded-xl mb-1 ${isMe ? 'bg-white/10' : 'bg-black/5'}`}>
              <div className={`p-2 rounded-full ${isMe ? 'bg-white/20' : 'bg-green-500/20 text-green-600'}`}>
                <Wallet className="w-6 h-6" />
              </div>
              <div className="flex-1 min-w-0 pr-4">
                <p className="font-bold text-lg leading-tight">{msg.payment.currency} {msg.payment.amount.toFixed(2)}</p>
                <p className="text-xs opacity-80 capitalize">{msg.payment.status}</p>
              </div>
            </div>
          )}

          {/* Text Content */}
          {msg.text && (
            <p className={`text-[15px] leading-relaxed ${isMe ? 'text-white' : 'text-on-surface'}`}>{msg.text}</p>
          )}
        </div>

        {/* Reactions */}
        {msg.reactions && Object.keys(msg.reactions).length > 0 && (
          <div className={`absolute -bottom-3 ${isMe ? 'right-4' : 'left-4'} flex gap-1 z-10`}>
            {Object.entries(msg.reactions).map(([emoji, users]) => (
              <div key={emoji} onClick={(e) => { e.stopPropagation(); handleReact(emoji); }} className="bg-surface border border-white/20 shadow-sm rounded-full px-1.5 py-0.5 text-xs flex items-center gap-1 cursor-pointer hover:scale-110 transition-transform">
                <span>{emoji}</span>
                {users.length > 1 && <span className="text-[10px] font-bold text-on-surface-variant">{users.length}</span>}
              </div>
            ))}
          </div>
        )}
      </div>

      <div className={`flex items-center gap-1 ${isMe ? 'mr-1' : 'ml-1'} ${msg.reactions && Object.keys(msg.reactions).length > 0 ? 'mt-3' : ''}`}>
        {isSecret && <Timer className="w-3 h-3 text-green-500" />}
        {msg.isEdited && <span className="text-[10px] text-on-surface-variant italic mr-1">edited</span>}
        <span className="text-[10px] text-on-surface-variant">{msg.timestamp}</span>
        {isMe && <CheckCheck className="w-[14px] h-[14px] text-primary" />}
      </div>
    </motion.div>
  );
}
