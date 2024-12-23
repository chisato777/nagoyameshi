let maxDate = new Date();
 maxDate = maxDate.setMonth(maxDate.getMonth() + 3);
 
 // 来店日（カレンダー入力）
 flatpickr('#visitDate', {
   locale: 'ja',
   minDate: 'today',
   dateFormat: 'Y-m-d',
 });
 
 // 時間（タイムピッカー）
flatpickr('#visitTime', {
  enableTime: true,   
  noCalendar: true,    
  dateFormat: 'H:i',   
  time_24hr: true,    
});