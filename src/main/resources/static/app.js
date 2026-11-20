const $ = id => document.getElementById(id);
const api = async (url, opts = {}) => {
  const r = await fetch(url, { headers: { 'Content-Type': 'application/json' }, ...opts });
  if (!r.ok && r.status !== 404) throw new Error(r.status);
  return r.status === 204 || r.status === 404 ? null : r.json();
};
const esc = s => (s ?? '').toString().replace(/[&<>"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));

// ---- tabs ----
document.querySelectorAll('nav button').forEach(b => b.onclick = () => {
  document.querySelectorAll('nav button').forEach(x => x.classList.remove('active')); b.classList.add('active');
  document.querySelectorAll('main > section').forEach(s => s.hidden = s.id !== b.dataset.tab);
  ({ dash: loadDash, tasks: loadTasks, notes: loadNotes, journal: loadJournal })[b.dataset.tab]();
});

// ---- dashboard ----
async function loadDash() {
  const d = await api('/api/dashboard');
  $('progress').textContent = d.progressPercent + '%'; $('overdue').textContent = d.overdueCount; $('total').textContent = d.totalTasks;
  const li = t => `<li><span>${esc(t.title)} <span class="tag">${t.priority}</span></span><span>${t.dueDate ?? ''}</span></li>`;
  $('todayList').innerHTML = d.todayTasks.map(li).join('') || '<li>Nothing due today 🎉</li>';
  $('upcomingList').innerHTML = d.upcoming.map(li).join('') || '<li>No upcoming deadlines</li>';
  $('recentNotes').innerHTML = d.recentNotes.map(n => `<li>${esc(n.title)}</li>`).join('') || '<li>No notes yet</li>';
}

// ---- tasks ----
async function loadTasks() {
  const p = new URLSearchParams();
  [['status','fStatus'],['priority','fPriority'],['category','fCategory'],['dueDate','fDue']].forEach(([k, id]) => { if ($(id).value) p.set(k, $(id).value); });
  const tasks = await api('/api/tasks?' + p);
  $('taskList').innerHTML = tasks.map(t => `
    <li class="${t.status === 'COMPLETED' ? 'done' : ''}">
      <span class="t">${esc(t.title)}
        <span class="tag">${t.status}</span><span class="tag">${t.priority}</span>
        ${t.category ? `<span class="tag">${esc(t.category)}</span>` : ''}
        ${t.dueDate ? `<span class="tag">📅 ${t.dueDate}</span>` : ''}
        ${t.reminderTime ? `<span class="tag">⏰ ${t.reminderTime.replace('T', ' ')}</span>` : ''}</span>
      <span><button onclick="cycle(${t.id})">Next status</button> <button class="small" onclick="delTask(${t.id})">✕</button></span>
    </li>`).join('') || '<li>No tasks found</li>';
}
$('applyFilter').onclick = loadTasks;
$('taskForm').onsubmit = async e => {
  e.preventDefault();
  await api('/api/tasks', { method: 'POST', body: JSON.stringify({
    title: $('tTitle').value, priority: $('tPriority').value, status: $('tStatus').value,
    category: $('tCategory').value || null, dueDate: $('tDue').value || null, reminderTime: $('tRemind').value || null }) });
  e.target.reset(); loadTasks();
};
async function cycle(id) {
  const t = await api('/api/tasks/' + id);
  const order = ['PENDING', 'IN_PROGRESS', 'COMPLETED'];
  t.status = order[(order.indexOf(t.status) + 1) % 3];
  await api('/api/tasks/' + id, { method: 'PUT', body: JSON.stringify(t) }); loadTasks();
}
async function delTask(id) { await api('/api/tasks/' + id, { method: 'DELETE' }); loadTasks(); }

// ---- notes ----
async function loadNotes() {
  const notes = await api('/api/notes?q=' + encodeURIComponent($('nSearch').value));
  $('noteList').innerHTML = notes.map(n => `<li><span><b>${esc(n.title)}</b><br>${esc(n.content)}</span>
    <button class="small" onclick="delNote(${n.id})">✕</button></li>`).join('') || '<li>No notes</li>';
}
$('nSearch').oninput = loadNotes;
$('noteForm').onsubmit = async e => {
  e.preventDefault();
  await api('/api/notes', { method: 'POST', body: JSON.stringify({ title: $('nTitle').value, content: $('nContent').value }) });
  e.target.reset(); loadNotes();
};
async function delNote(id) { await api('/api/notes/' + id, { method: 'DELETE' }); loadNotes(); }

// ---- journal ----
async function loadJournal() {
  if (!$('jDate').value) $('jDate').value = new Date().toISOString().slice(0, 10);
  const e = await api('/api/journal/' + $('jDate').value);
  $('jContent').value = e ? e.content : ''; $('jMood').value = e ? (e.mood ?? '') : '';
}
$('jDate').onchange = loadJournal;
$('jSave').onclick = async () => {
  await api('/api/journal/' + $('jDate').value, { method: 'PUT', body: JSON.stringify({ content: $('jContent').value, mood: $('jMood').value || null }) });
  alert('Saved');
};
$('jDelete').onclick = async () => { await api('/api/journal/' + $('jDate').value, { method: 'DELETE' }); loadJournal(); };

loadDash();
