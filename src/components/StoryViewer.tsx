import React, { useState, useEffect, useCallback } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { X, Send, Heart, Share2, MoreHorizontal, MessageCircle } from 'lucide-react';
import { useAppContext, Story } from '../context/AppContext';

interface StoryViewerProps {
  stories: Story[];
  initialIndex: number;
  onClose: () => void;
}

export default function StoryViewer({ stories, initialIndex, onClose }: StoryViewerProps) {
  const { addStoryComment } = useAppContext();
  const [currentIndex, setCurrentIndex] = useState(initialIndex);
  const [progress, setProgress] = useState(0);
  const [isPaused, setIsPaused] = useState(false);
  const [replyText, setReplyText] = useState('');

  const currentStory = stories[currentIndex];

  const handleNext = useCallback(() => {
    if (currentIndex < stories.length - 1) {
      setCurrentIndex(prev => prev + 1);
      setProgress(0);
    } else {
      onClose();
    }
  }, [currentIndex, stories.length, onClose]);

  const handlePrev = useCallback(() => {
    if (currentIndex > 0) {
      setCurrentIndex(prev => prev - 1);
      setProgress(0);
    }
  }, [currentIndex]);

  const handleSendReply = () => {
    if (currentStory && replyText.trim()) {
      addStoryComment(currentStory.id, replyText);
      setReplyText('');
    }
  };

  useEffect(() => {
    if (isPaused) return;

    const duration = 5000; // 5 seconds per story
    const interval = 50; 
    const step = (interval / duration) * 100;

    const timer = setInterval(() => {
      setProgress(prev => {
        if (prev + step >= 100) {
          clearInterval(timer);
          handleNext();
          return 100;
        }
        return prev + step;
      });
    }, interval);

    return () => clearInterval(timer);
  }, [currentIndex, isPaused, handleNext]);

  return (
    <AnimatePresence>
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        exit={{ opacity: 0, scale: 0.9 }}
        className="fixed inset-0 z-[1000] bg-black text-white flex flex-col items-center justify-center"
      >
        <div className="relative w-full max-w-lg h-full md:h-[90vh] md:rounded-[3rem] overflow-hidden bg-neutral-900 shadow-2xl flex flex-col">
          {/* Progress Bars */}
          <div className="absolute top-4 left-4 right-4 z-20 flex gap-1.5">
            {stories.map((story, index) => (
              <div key={story.id} className="h-1 flex-1 bg-white/20 rounded-full overflow-hidden">
                <div
                  className="h-full bg-white transition-all duration-50 ease-linear"
                  style={{
                    width: `${index < currentIndex ? 100 : index === currentIndex ? progress : 0}%`
                  }}
                />
              </div>
            ))}
          </div>

          {/* Header */}
          <div className="absolute top-8 left-6 right-6 z-20 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-full border-2 border-white p-0.5">
                <img src={currentStory.userAvatar} alt="" className="w-full h-full rounded-full object-cover" />
              </div>
              <div className="flex flex-col">
                <span className="text-sm font-black text-white drop-shadow-md">{currentStory.userName}</span>
                <span className="text-[10px] font-bold text-white/60">{currentStory.timestamp}</span>
              </div>
            </div>
            <div className="flex items-center gap-4">
              <button className="text-white drop-shadow-md"><MoreHorizontal /></button>
              <button onClick={onClose} className="text-white drop-shadow-md"><X /></button>
            </div>
          </div>

          {/* Content */}
          <div 
            className="flex-1 relative group"
            onPointerDown={() => setIsPaused(true)}
            onPointerUp={() => setIsPaused(false)}
            onPointerLeave={() => setIsPaused(false)}
          >
            {currentStory.mediaType === 'image' ? (
              <img
                src={currentStory.mediaUrl}
                alt="Story"
                className="w-full h-full object-cover"
                referrerPolicy="no-referrer"
              />
            ) : (
              <video
                src={currentStory.mediaUrl}
                className="w-full h-full object-cover"
                autoPlay
                muted
                playsInline
                loop
              />
            )}

            {/* Tap Zones */}
            <div className="absolute inset-0 flex">
              <div className="w-1/3 h-full cursor-pointer" onClick={(e) => { e.stopPropagation(); handlePrev(); }} />
              <div className="w-2/3 h-full cursor-pointer" onClick={(e) => { e.stopPropagation(); handleNext(); }} />
            </div>
          </div>

          {/* Footer / Interaction */}
          <div className="absolute bottom-0 left-0 right-0 p-6 bg-gradient-to-t from-black/80 via-black/40 to-transparent pt-20">
            <div className="flex items-center gap-4 mb-6">
              <div className="flex-1 relative">
                <input 
                  type="text"
                  value={replyText}
                  onChange={(e) => setReplyText(e.target.value)}
                  onFocus={() => setIsPaused(true)}
                  onBlur={() => setIsPaused(false)}
                  placeholder="Send message..."
                  className="w-full bg-white/10 backdrop-blur-md border border-white/20 rounded-full px-6 py-3 text-white text-sm outline-none focus:bg-white/20 transition-all"
                />
                <button 
                  onClick={handleSendReply}
                  className="absolute right-2 top-1/2 -translate-y-1/2 w-8 h-8 rounded-full bg-primary text-white flex items-center justify-center"
                >
                  <Send className="w-4 h-4" />
                </button>
              </div>
              <button className="text-white active:scale-125 transition-transform"><Heart className="w-6 h-6" /></button>
              <button className="text-white active:scale-125 transition-transform"><Share2 className="w-6 h-6" /></button>
            </div>

            {/* Quick Reactions */}
            <div className="flex justify-between px-2">
              {['🔥', '😂', '😮', '😢', '😍', '👏'].map(emoji => (
                <button 
                  key={emoji}
                  onClick={() => addStoryComment(currentStory.id, emoji)}
                  className="text-2xl hover:scale-125 transition-transform active:scale-90"
                >
                  {emoji}
                </button>
              ))}
            </div>
          </div>
        </div>
      </motion.div>
    </AnimatePresence>
  );
}
