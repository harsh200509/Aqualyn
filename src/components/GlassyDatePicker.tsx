import React, { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Calendar, ChevronDown } from 'lucide-react';

interface GlassyDatePickerProps {
  value: string; // YYYY-MM-DD
  onChange: (date: string) => void;
}

const months = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'
];

export default function GlassyDatePicker({ value, onChange }: GlassyDatePickerProps) {
  const [isOpen, setIsOpen] = useState(false);
  
  // Parse initial value or use current date
  const initialDate = value ? new Date(value) : new Date();
  const [day, setDay] = useState(value ? initialDate.getDate() : '');
  const [month, setMonth] = useState(value ? initialDate.getMonth() : '');
  const [year, setYear] = useState(value ? initialDate.getFullYear() : '');

  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    if (day !== '' && month !== '' && year !== '') {
      const formattedMonth = String(Number(month) + 1).padStart(2, '0');
      const formattedDay = String(day).padStart(2, '0');
      onChange(`${year}-${formattedMonth}-${formattedDay}`);
    }
  }, [day, month, year]);

  const currentYear = new Date().getFullYear();
  const years = Array.from({ length: 100 }, (_, i) => currentYear - i);
  const daysInMonth = month !== '' && year !== '' ? new Date(Number(year), Number(month) + 1, 0).getDate() : 31;
  const days = Array.from({ length: daysInMonth }, (_, i) => i + 1);

  const displayValue = value 
    ? `${months[Number(month)]} ${day}, ${year}`
    : 'Select Date of Birth';

  return (
    <div className="relative" ref={containerRef}>
      <div 
        onClick={() => setIsOpen(!isOpen)}
        className="w-full h-14 bg-white/40 border-outline-variant/20 border rounded-2xl pl-12 pr-4 flex items-center justify-between cursor-pointer hover:bg-white/50 transition-all shadow-inner group"
      >
        <div className="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-hover:text-secondary transition-colors">
          <Calendar className="w-5 h-5" />
        </div>
        <span className={`font-body ${value ? 'text-on-surface' : 'text-on-surface-variant/50'}`}>
          {displayValue}
        </span>
        <ChevronDown className={`w-5 h-5 text-on-surface-variant transition-transform duration-300 ${isOpen ? 'rotate-180' : ''}`} />
      </div>

      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: -10, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: -10, scale: 0.95 }}
            transition={{ type: 'spring', damping: 25, stiffness: 300 }}
            className="absolute top-full left-0 w-full mt-2 p-4 bg-surface/90 backdrop-blur-xl border border-white/20 rounded-[2rem] shadow-[0_8px_32px_0_rgba(0,87,189,0.15)] z-50 flex gap-2"
          >
            {/* Month Column */}
            <div className="flex-1 h-48 overflow-y-auto custom-scrollbar snap-y snap-mandatory bg-black/5 rounded-xl border border-white/10 relative">
              <div className="absolute top-1/2 left-0 w-full h-10 -translate-y-1/2 bg-secondary/10 border-y border-secondary/20 pointer-events-none" />
              <div className="py-[76px]">
                {months.map((m, i) => (
                  <div 
                    key={m}
                    onClick={() => setMonth(i)}
                    className={`h-10 flex items-center justify-center cursor-pointer snap-center transition-colors ${month === i ? 'text-secondary font-bold text-lg' : 'text-on-surface-variant hover:text-on-surface'}`}
                  >
                    {m.substring(0, 3)}
                  </div>
                ))}
              </div>
            </div>

            {/* Day Column */}
            <div className="flex-1 h-48 overflow-y-auto custom-scrollbar snap-y snap-mandatory bg-black/5 rounded-xl border border-white/10 relative">
              <div className="absolute top-1/2 left-0 w-full h-10 -translate-y-1/2 bg-secondary/10 border-y border-secondary/20 pointer-events-none" />
              <div className="py-[76px]">
                {days.map(d => (
                  <div 
                    key={d}
                    onClick={() => setDay(d)}
                    className={`h-10 flex items-center justify-center cursor-pointer snap-center transition-colors ${day === d ? 'text-secondary font-bold text-lg' : 'text-on-surface-variant hover:text-on-surface'}`}
                  >
                    {d}
                  </div>
                ))}
              </div>
            </div>

            {/* Year Column */}
            <div className="flex-1 h-48 overflow-y-auto custom-scrollbar snap-y snap-mandatory bg-black/5 rounded-xl border border-white/10 relative">
              <div className="absolute top-1/2 left-0 w-full h-10 -translate-y-1/2 bg-secondary/10 border-y border-secondary/20 pointer-events-none" />
              <div className="py-[76px]">
                {years.map(y => (
                  <div 
                    key={y}
                    onClick={() => setYear(y)}
                    className={`h-10 flex items-center justify-center cursor-pointer snap-center transition-colors ${year === y ? 'text-secondary font-bold text-lg' : 'text-on-surface-variant hover:text-on-surface'}`}
                  >
                    {y}
                  </div>
                ))}
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
