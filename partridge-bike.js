/* 鹧鸪骑行记 —— 动态 SVG 场景脚本 */
'use strict';
const NS = 'http://www.w3.org/2000/svg';
const $ = id => document.getElementById(id);
const TAU = Math.PI * 2;
const svg = $('scene');

function el(tag, attrs, parent) {
  const e = document.createElementNS(NS, tag);
  if (attrs) for (const k in attrs) e.setAttribute(k, attrs[k]);
  if (parent) parent.appendChild(e);
  return e;
}

// 固定种子的随机数，保证每次打开画面一致
let seed = 20260927;
function rand() {
  seed = (seed + 0x6D2B79F5) | 0;
  let t = Math.imul(seed ^ (seed >>> 15), 1 | seed);
  t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
  return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
}
const R = (a, b) => a + (b - a) * rand();
const clamp = (v, a, b) => (v < a ? a : v > b ? b : v);
const smooth = (a, b, v) => { const t = clamp((v - a) / (b - a), 0, 1); return t * t * (3 - 2 * t); };
const f1 = n => n.toFixed(1);

/* ---------------- 调色板：昼夜关键帧 ---------------- */
const hx = h => [parseInt(h.substr(1, 2), 16), parseInt(h.substr(3, 2), 16), parseInt(h.substr(5, 2), 16)];
const mixc = (a, b, t) => [a[0] + (b[0] - a[0]) * t, a[1] + (b[1] - a[1]) * t, a[2] + (b[2] - a[2]) * t];
const mulc = (a, l) => [a[0] * l[0] / 255, a[1] * l[1] / 255, a[2] * l[2] / 255];
const rgb = c => `rgb(${Math.round(c[0])},${Math.round(c[1])},${Math.round(c[2])})`;

const COLK = ['sky1', 'sky2', 'sky3', 'light', 'fog', 'sun', 'glow', 'cloud', 'cloud2'];
const NIGHT = { sky1: '#060a1e', sky2: '#0f1938', sky3: '#253462', light: '#3e4f80', fog: '#141d3b', sun: '#ff7a45', glow: '#000000', cloud: '#27324f', cloud2: '#161d33', night: 1, mist: .4 };
const DAWN = { sky1: '#2d3a6e', sky2: '#b06f8c', sky3: '#f7a77f', light: '#e3a391', fog: '#d99a92', sun: '#ffb36b', glow: '#ff9a5c', cloud: '#f2a99b', cloud2: '#8d6a8c', night: .3, mist: .9 };
const KEYS = [
  [0.00, DAWN],
  [0.06, { sky1: '#4f7fc4', sky2: '#a9c3e3', sky3: '#fbd9b8', light: '#fde6cf', fog: '#e9dccf', sun: '#ffe0a3', glow: '#ffcf8a', cloud: '#fff4ea', cloud2: '#c9b7c2', night: 0, mist: .95 }],
  [0.16, { sky1: '#3f86d8', sky2: '#8fc2ef', sky3: '#dcecf7', light: '#ffffff', fog: '#d3e4f0', sun: '#fff6d8', glow: '#fff2c0', cloud: '#ffffff', cloud2: '#cfdced', night: 0, mist: .6 }],
  [0.30, { sky1: '#3a7fd5', sky2: '#86bdf0', sky3: '#d7ecfa', light: '#fffdf7', fog: '#cfe3f2', sun: '#fffbe6', glow: '#fff5cc', cloud: '#ffffff', cloud2: '#d2deef', night: 0, mist: .45 }],
  [0.42, { sky1: '#4a7cc6', sky2: '#a9c4df', sky3: '#f6dcae', light: '#fff0d2', fog: '#eadcc2', sun: '#fff0c0', glow: '#ffe39a', cloud: '#fff6e6', cloud2: '#d9c9c4', night: 0, mist: .5 }],
  [0.52, { sky1: '#3b4d8f', sky2: '#e39a86', sky3: '#ffc47a', light: '#ffc796', fog: '#f0b58f', sun: '#ffb85c', glow: '#ff9b4a', cloud: '#ffc8a0', cloud2: '#b67d8c', night: .05, mist: .55 }],
  [0.59, { sky1: '#2b2f66', sky2: '#c0607a', sky3: '#ff9a5a', light: '#e98f7a', fog: '#c77b82', sun: '#ff8a45', glow: '#ff6a3a', cloud: '#f59480', cloud2: '#7d4f73', night: .25, mist: .6 }],
  [0.65, { sky1: '#151a42', sky2: '#433a72', sky3: '#a45c79', light: '#77668f', fog: '#5c4f7a', sun: '#ff7a45', glow: '#c2556b', cloud: '#6e577e', cloud2: '#3b3158', night: .7, mist: .55 }],
  [0.72, NIGHT],
  [0.90, NIGHT],
  [0.96, { sky1: '#1a2150', sky2: '#43406f', sky3: '#9a6680', light: '#8a7596', fog: '#6d5a7f', sun: '#ffb36b', glow: '#d0706b', cloud: '#8b6b88', cloud2: '#4a3b5e', night: .6, mist: .7 }],
  [1.00, DAWN],
].map(([t, o]) => { const k = { t, night: o.night, mist: o.mist }; COLK.forEach(c => { k[c] = hx(o[c]); }); return k; });

// 物体固有色：[颜色, 雾化程度, 是否属于骑手]
const BASE = {
  m1: ['#7d9bb5', .55], m2: ['#5f8aa0', .42], m3: ['#3f7563', .28], m3d: ['#2c5a4b', .28], pag: ['#5b4d47', .28],
  hill1: ['#8cc26a', .16], hill2: ['#6aab55', .1], terr: ['#a6d67f', .1], rape: ['#f4d23c', .1], rape2: ['#d9b21f', .1],
  tree1: ['#3d8b4a', .14], tree2: ['#5fa855', .14], wall: ['#f4f1e8', .14], roof: ['#3b4049', .14],
  meadow: ['#7dbb5c', .04], trunk: ['#6b4a36', .03], bl1: ['#f59ab8', .03], bl2: ['#fbc6d6', .03], bl3: ['#ec7aa3', .03], blh: ['#fff0f5', .03],
  wil: ['#a3d160', .03], wil2: ['#6f9f3f', .03], pil: ['#b8412e', .03], tile: ['#3f444d', .03], stone: ['#a7a49b', .03], bush: ['#4f9a45', .03],
  road: ['#dcc49b', 0], roadHi: ['#ecdcb9', 0], roadLo: ['#b89c74', 0], peb: ['#a88f6d', 0], pebHi: ['#efe2c8', 0], wood: ['#a0764f', 0], wood2: ['#6d4c33', 0],
  grass: ['#3f8a36', 0], grass2: ['#62ad4c', 0], ground: ['#2f6e2e', 0],
  fw: ['#ffffff', 0], fy: ['#ffd23f', 0], fr: ['#e8484b', 0], fb: ['#7b95ff', 0], fp: ['#f08ac0', 0],
  egret: ['#ffffff', .2],
  bike: ['#e0533d', 0, 1], bike2: ['#a8392a', 0, 1], tire: ['#2b2b30', 0, 1], rim: ['#d5dbe3', 0, 1], metal: ['#aeb6c0', 0, 1], mdark: ['#5f6670', 0, 1],
  fender: ['#f3e6c9', 0, 1], saddle: ['#5a3a28', 0, 1], bask: ['#c99a5c', 0, 1], bask2: ['#8e6431', 0, 1],
  body: ['#2e2724', 0, 1], body2: ['#3d312b', 0, 1], spot: ['#fbf5ea', 0, 1], wing: ['#a0582f', 0, 1], wing2: ['#6f3a1c', 0, 1], wspot: ['#f0d9a8', 0, 1],
  chest: ['#c0692f', 0, 1], cheek: ['#fffaf2', 0, 1], beak: ['#3b3533', 0, 1], leg: ['#f2a33a', 0, 1], legF: ['#c77d23', 0, 1], thighF: ['#1e1815', 0, 1],
  scarf: ['#e4443b', 0, 1], scarf2: ['#b52f2b', 0, 1], chick: ['#e0b073', 0, 1], chick2: ['#a8773f', 0, 1], blush: ['#ff9aa8', 0, 1], eyeW: ['#ffffff', 0, 1],
  fl1: ['#ff8fb4', 0, 1], fl2: ['#ffd84a', 0, 1], leaf: ['#5aa04a', 0, 1],
};
for (const k in BASE) BASE[k] = { c: hx(BASE[k][0]), f: BASE[k][1], r: !!BASE[k][2] };

function sample(t) {
  let i = 0;
  while (i < KEYS.length - 2 && t >= KEYS[i + 1].t) i++;
  const a = KEYS[i], b = KEYS[i + 1];
  let f = clamp((t - a.t) / (b.t - a.t), 0, 1);
  f = f * f * (3 - 2 * f);
  const o = {};
  COLK.forEach(k => { o[k] = mixc(a[k], b[k], f); });
  o.night = a.night + (b.night - a.night) * f;
  o.mist = a.mist + (b.mist - a.mist) * f;
  return o;
}

let P = null; // 当前调色板
function applyPalette() {
  P = sample(tod);
  const st = svg.style;
  COLK.forEach(k => { if (k !== 'light') st.setProperty('--' + k, rgb(P[k])); });
  const riderLight = mixc(P.light, [255, 255, 255], .25 * P.night);
  const lit = {};
  for (const k in BASE) {
    const b = BASE[k];
    let c = mulc(b.c, b.r ? riderLight : P.light);
    if (b.f) c = mixc(c, P.fog, b.f);
    lit[k] = c;
    st.setProperty('--' + k, rgb(c));
  }
  st.setProperty('--m1b', rgb(mixc(lit.m1, P.fog, .65)));
  st.setProperty('--m2b', rgb(mixc(lit.m2, P.fog, .55)));
  st.setProperty('--m3b', rgb(mixc(lit.m3, P.fog, .45)));
  st.setProperty('--water1', rgb(mixc(P.sky3, P.sky2, .35)));
  st.setProperty('--water2', rgb(mulc(mixc(P.sky2, P.sky1, .55), [190, 210, 225])));
  st.setProperty('--win', rgb(mixc(mulc([74, 81, 96], P.light), [255, 210, 122], P.night)));
  st.setProperty('--night', P.night.toFixed(3));
  st.setProperty('--mist', P.mist.toFixed(3));
}

/* ---------------- 视差图层 ---------------- */
const layers = [];
function layer(id, speed, tile, build) {
  const g = $(id);
  const t = el('g', null, g);
  build(t, tile);
  const c = t.cloneNode(true);
  c.setAttribute('transform', `translate(${tile},0)`);
  g.appendChild(c);
  layers.push({ g, speed, tile });
}
function mirror(srcId, dstId, speed, tile) {
  const src = $(srcId), dst = $(dstId);
  for (const ch of Array.from(src.children)) dst.appendChild(ch.cloneNode(true));
  layers.push({ g: dst, speed, tile });
}
function curve(f, T, bottom, step) {
  step = step || 8;
  let d = `M-2,${bottom} L-2,${f1(f(-2))}`;
  for (let x = 0; x <= T; x += step) d += ` L${x},${f1(f(x))}`;
  return d + ` L${T + 2},${f1(f(T + 2))} L${T + 2},${bottom} Z`;
}
const S = name => `fill:var(--${name})`;

// 喀斯特峰林的山脊函数（周期为 T，可无缝拼接）
function karst(T, n, h0, h1, w0, w1, base) {
  const pk = [];
  for (let i = 0; i < n; i++) pk.push([(i + R(-.3, .3)) * T / n, R(h0, h1), R(w0, w1)]);
  const p1 = R(0, TAU), p2 = R(0, TAU);
  return x => {
    let m = 18;
    for (const [p, h, w] of pk) {
      const d = ((x - p) % T + T * 1.5) % T - T / 2;
      const v = h * Math.exp(-Math.pow(Math.abs(d) / w, 2.2));
      if (v > m) m = v;
    }
    return base - m - 5 * Math.sin(TAU * 7 * x / T + p1) - 2.5 * Math.sin(TAU * 23 * x / T + p2);
  };
}

function pagoda(g, x, y) {
  for (let i = 0; i < 5; i++) {
    const w = 16 - i * 2.4, yy = y - i * 9;
    el('rect', { x: f1(x - w / 2), y: f1(yy - 7), width: f1(w), height: 7, style: S('pag') }, g);
    el('path', { d: `M${f1(x - w / 2 - 4)},${f1(yy - 6)} L${f1(x + w / 2 + 4)},${f1(yy - 6)} L${f1(x + w / 2)},${f1(yy - 9)} L${f1(x - w / 2)},${f1(yy - 9)} Z`, style: S('pag') }, g);
  }
  el('rect', { x: f1(x - .8), y: f1(y - 54), width: 1.6, height: 10, style: S('pag') }, g);
  el('circle', { cx: f1(x), cy: f1(y - 22), r: 16, fill: 'url(#gWin)', style: 'opacity:var(--night)' }, g);
}

function roundTree(g, x, y, r) {
  el('rect', { x: f1(x - 1.3), y: f1(y - r * .9), width: 2.6, height: f1(r * .9), style: S('trunk') }, g);
  el('circle', { cx: f1(x), cy: f1(y - r * 1.3), r: f1(r), style: S('tree1') }, g);
  el('circle', { cx: f1(x - r * .3), cy: f1(y - r * 1.55), r: f1(r * .55), style: S('tree2') }, g);
}

// 白墙黛瓦的小民居，夜里窗户会亮
function house(g, x, y, big) {
  const w = big ? 44 : 36, h = big ? 26 : 22, L = x - w / 2, T = y - h;
  el('rect', { x: L, y: T, width: w, height: h + 4, style: S('wall') }, g);
  el('path', { d: `M${L - 6},${T + 2} Q${L},${T - 3} ${L + 4},${T - 9} L${L + w - 4},${T - 9} Q${L + w},${T - 3} ${L + w + 6},${T + 2} Z`, style: S('roof') }, g);
  el('rect', { x: L - 1, y: T - 15, width: 7, height: 16, style: S('wall') }, g);
  el('rect', { x: L - 2, y: T - 17, width: 9, height: 3, style: S('roof') }, g);
  el('rect', { x: L + w - 6, y: T - 15, width: 7, height: 16, style: S('wall') }, g);
  el('rect', { x: L + w - 7, y: T - 17, width: 9, height: 3, style: S('roof') }, g);
  el('rect', { x: x - 3.5, y: y - 10, width: 7, height: 14, style: S('roof') }, g);
  for (const wx of [L + 6, L + w - 12]) {
    el('circle', { cx: wx + 3, cy: T + 10, r: 15, fill: 'url(#gWin)', style: 'opacity:var(--night)' }, g);
    el('rect', { x: wx, y: T + 7, width: 6, height: 6, style: S('win') }, g);
  }
  if (big) for (let i = 0; i < 3; i++)
    el('circle', { cx: x + 8, cy: T - 18, r: 5, class: 'smoke', style: `fill:var(--cloud2);animation-delay:${-i * 2}s` }, g);
}

function patch(g, f, x0, x1) {
  const n = 30; let top = '', bot = '';
  const e = i => Math.pow(Math.sin(Math.PI * i / n), .5);
  for (let i = 0; i <= n; i++) { const x = x0 + (x1 - x0) * i / n; top += (i ? ' L' : 'M') + f1(x) + ',' + f1(f(x) + 6); }
  for (let i = n; i >= 0; i--) { const x = x0 + (x1 - x0) * i / n; bot += ' L' + f1(x) + ',' + f1(f(x) + 6 + 44 * e(i)); }
  el('path', { d: top + bot + ' Z', style: S('rape') }, g);
  for (const k of [.35, .65]) {
    let d = '';
    for (let i = 2; i <= n - 2; i++) { const x = x0 + (x1 - x0) * i / n; d += (i > 2 ? ' L' : 'M') + f1(x) + ',' + f1(f(x) + 6 + 44 * e(i) * k); }
    el('path', { d, style: 'fill:none;stroke:var(--rape2);stroke-width:2;opacity:.6' }, g);
  }
}

function peach(g) {
  const s = el('g', { class: 'tsway', style: `animation-delay:${f1(-R(0, 5))}s` }, g);
  el('path', { d: 'M-5,0 C-3,-30 -7,-50 -2,-78 L4,-78 C4,-50 6,-30 7,0 Z', style: S('trunk') }, s);
  el('path', { d: 'M0,-70 C-14,-86 -30,-92 -46,-108 M1,-76 C14,-92 28,-100 44,-116 M1,-76 C0,-98 4,-116 0,-138 M-18,-86 C-22,-98 -18,-110 -24,-124', style: 'fill:none;stroke:var(--trunk);stroke-width:4.5;stroke-linecap:round' }, s);
  const cl = [[-46, -112, 28], [-24, -130, 32], [2, -146, 34], [28, -128, 30], [46, -114, 24], [-6, -108, 28], [18, -100, 22]];
  for (const [x, y, r] of cl) el('circle', { cx: x + 3, cy: y + 4, r, style: S('bl3') }, s);
  for (const [x, y, r] of cl) el('circle', { cx: x, cy: y, r: f1(r * .92), style: S('bl1') }, s);
  for (const [x, y, r] of cl) {
    el('circle', { cx: f1(x - r * .3), cy: f1(y - r * .3), r: f1(r * .45), style: S('bl2') }, s);
    el('circle', { cx: f1(x - r * .45), cy: f1(y - r * .45), r: f1(r * .14), style: S('blh') }, s);
  }
  for (let i = 0; i < 10; i++) el('circle', { cx: f1(R(-60, 60)), cy: f1(R(-165, -95)), r: f1(R(1.5, 3)), style: S('blh') }, s);
}

function willow(g) {
  el('path', { d: 'M-9,0 C-6,-40 -16,-70 -6,-112 L6,-112 C4,-72 14,-40 11,0 Z', style: S('trunk') }, g);
  el('path', { d: 'M-2,-104 C-20,-120 -40,-126 -62,-128 M2,-106 C20,-124 42,-128 64,-126 M0,-110 C-4,-128 2,-140 0,-150', style: 'fill:none;stroke:var(--trunk);stroke-width:5;stroke-linecap:round' }, g);
  for (const [x, y, r] of [[-40, -132, 22], [0, -150, 28], [40, -132, 22], [-18, -142, 20], [20, -144, 20]])
    el('circle', { cx: x, cy: y, r, style: S('wil2') }, g);
  const sw = el('g', { class: 'wsway', style: `animation-delay:${f1(-R(0, 4))}s` }, g);
  let d = '';
  for (let i = 0; i < 28; i++) {
    const x0 = R(-68, 68), y0 = -128 - R(0, 24) + Math.abs(x0) * .25, len = R(60, 112), dx = R(-8, 8);
    d += `M${f1(x0)},${f1(y0)} Q${f1(x0 + dx)},${f1(y0 + len * .5)} ${f1(x0 + dx * 1.6)},${f1(y0 + len)} `;
  }
  el('path', { d, style: 'fill:none;stroke:var(--wil2);stroke-width:1.4' }, sw);
  el('path', { d, style: 'fill:none;stroke:var(--wil);stroke-width:4;stroke-linecap:round;stroke-dasharray:5 4' }, sw);
}

function bush(g) {
  for (const [x, y, r, c] of [[-22, -12, 16, 'bush'], [0, -20, 20, 'bush'], [22, -12, 15, 'bush'], [-6, -24, 10, 'wil'], [12, -18, 8, 'wil']])
    el('circle', { cx: x, cy: y, r, style: S(c) }, g);
}

function pavilion(g) {
  el('rect', { x: -48, y: -10, width: 96, height: 12, style: S('stone') }, g);
  for (const x of [-36, 29]) el('rect', { x, y: -76, width: 7, height: 66, style: S('pil') }, g);
  el('rect', { x: -40, y: -80, width: 80, height: 8, style: S('pil') }, g);
  el('rect', { x: -36, y: -32, width: 72, height: 4, style: S('pil') }, g);
  el('path', { d: 'M-74,-90 Q-60,-78 -44,-78 L44,-78 Q60,-78 74,-90 Q54,-94 36,-106 L4,-130 L-4,-130 L-36,-106 Q-54,-94 -74,-90 Z', style: S('tile') }, g);
  el('circle', { cx: 0, cy: -135, r: 4.5, style: S('tile') }, g);
}

function flower(g, x, b) {
  const h = R(22, 44), type = Math.floor(rand() * 4);
  el('path', { d: `M${f1(x)},${f1(b)} Q${f1(x + 3)},${f1(b - h / 2)} ${f1(x + 1)},${f1(b - h)}`, style: 'fill:none;stroke:var(--grass);stroke-width:1.6' }, g);
  const fx = x + 1, fy = b - h;
  if (type === 0) {
    el('circle', { cx: f1(fx), cy: f1(fy), r: 4.2, style: 'fill:none;stroke:var(--fw);stroke-width:4.6;stroke-dasharray:2.6 1.6' }, g);
    el('circle', { cx: f1(fx), cy: f1(fy), r: 2.6, style: S('fy') }, g);
  } else if (type === 1) {
    el('circle', { cx: f1(fx), cy: f1(fy), r: 5.5, style: S('fr') }, g);
    el('circle', { cx: f1(fx), cy: f1(fy), r: 1.8, fill: '#2a1a1a' }, g);
  } else if (type === 2) {
    el('circle', { cx: f1(fx), cy: f1(fy), r: 4.2, style: S('fb') }, g);
    el('circle', { cx: f1(fx), cy: f1(fy), r: 1.4, style: S('fw') }, g);
  } else {
    for (let i = 0; i < 3; i++) el('circle', { cx: f1(fx + R(-4, 4)), cy: f1(fy + R(-4, 3)), r: f1(R(2.2, 3.2)), style: S('fp') }, g);
  }
}

/* ---------------- 构建场景 ---------------- */
layer('mtn1', .03, 1600, (g, T) => {
  el('path', { d: curve(karst(T, 13, 150, 300, 38, 72, 568), T, 572, 6), fill: 'url(#gM1)' }, g);
});
layer('mtn2', .065, 1600, (g, T) => {
  el('path', { d: curve(karst(T, 11, 100, 215, 34, 62, 568), T, 572, 6), fill: 'url(#gM2)' }, g);
});
layer('mtn3', .13, 1600, (g, T) => {
  const f = karst(T, 8, 70, 170, 44, 84, 568);
  el('path', { d: curve(f, T, 572, 6), fill: 'url(#gM3)' }, g);
  for (let i = 0; i < 110; i++) {
    const x = R(10, T - 10), y = f(x) + R(5, 80);
    if (y < 556) el('circle', { cx: f1(x), cy: f1(y), r: f1(R(1.8, 4.2)), style: S('m3d'), opacity: .55 }, g);
  }
  let bx = 400, by = 1e9;
  for (let x = 300; x < 1300; x += 4) { const y = f(x); if (y < by) { by = y; bx = x; } }
  pagoda(g, bx, by + 3);
});
mirror('mtn2', 'rm2', .065, 1600);
mirror('mtn3', 'rm3', .13, 1600);

layer('puffs', .09, 1600, g => {
  for (let i = 0; i < 6; i++)
    el('ellipse', { cx: f1(R(150, 1450)), cy: f1(R(505, 560)), rx: f1(R(180, 320)), ry: f1(R(18, 34)), fill: 'url(#gPuff)' }, g);
});

// 水面：切断倒影的横纹 + 闪烁波光 + 日月倒影
for (let y = 566; y < 700; y += R(7, 12))
  el('rect', { x: 0, y: f1(y), width: 1600, height: f1(R(.8, 1.8)), style: 'fill:var(--water1)', opacity: .6 }, $('wlines'));
layer('shimmer', .18, 1600, g => {
  for (let i = 0; i < 60; i++)
    el('rect', { x: f1(R(0, 1560)), y: f1(R(564, 660)), width: f1(R(8, 40)), height: 1.4, rx: .7, fill: '#fff', class: 'tw', style: `animation-delay:${f1(-R(0, 3))}s;animation-duration:${f1(R(1.5, 3.5))}s` }, g);
});
const glitter = $('glitter');
for (let i = 0; i < 10; i++) {
  const w = 70 - i * 5 + R(-8, 8);
  el('rect', { x: f1(-w / 2 + R(-6, 6)), y: 565 + i * 9, width: f1(w), height: 2.2, rx: 1.1, class: 'tw', style: `animation-delay:${f1(-R(0, 2))}s;animation-duration:${f1(R(.8, 1.8))}s` }, glitter);
}

layer('hills', .28, 2400, (g, T) => {
  const yb = x => 654 + 14 * Math.sin(TAU * 2 * x / T + .5) + 9 * Math.sin(TAU * 5 * x / T + 1.3) + 5 * Math.sin(TAU * 11 * x / T + 2.1);
  const yf = x => 690 + 12 * Math.sin(TAU * 3 * x / T + 2) + 8 * Math.sin(TAU * 7 * x / T + .2) + 4 * Math.sin(TAU * 13 * x / T + 1);
  el('path', { d: curve(yb, T, 900), style: S('hill1') }, g);
  const village = x => (x > 590 && x < 820) || (x > 1530 && x < 1700);
  for (let i = 0; i < 44; i++) { const x = R(20, T - 20); if (!village(x)) roundTree(g, x, yb(x) + 3, R(6, 11)); }
  for (const [x, big] of [[630, 0], [690, 1], [760, 0], [1580, 1], [1648, 0]]) house(g, x, yb(x) + 8, big);
  el('path', { d: curve(yf, T, 900), style: S('hill2') }, g);
  for (const k of [16, 32, 50]) {
    let d = '';
    for (let x = 0; x <= T; x += 10) d += (x ? ' L' : 'M') + x + ',' + f1(yf(x) + k);
    el('path', { d, style: 'fill:none;stroke:var(--terr);stroke-width:1.6;opacity:.45' }, g);
  }
  for (const [a, b] of [[180, 520], [1080, 1380], [1800, 2120]]) patch(g, yf, a, b);
  for (let i = 0; i < 14; i++) { const x = R(30, T - 30); roundTree(g, x, yf(x) + 4, R(10, 15)); }
});

layer('grove', .55, 2000, (g, T) => {
  const ym = x => 727 + 5 * Math.sin(TAU * 3 * x / T) + 3 * Math.sin(TAU * 8 * x / T + 1);
  el('path', { d: curve(ym, T, 900), style: S('meadow') }, g);
  const items = [[90, peach, 1], [330, willow, 1.05], [560, bush, 1], [680, peach, .85], [900, peach, 1.1], [1150, willow, .95],
    [1330, bush, .9], [1560, pavilion, 1], [1790, peach, 1], [1930, bush, .7]];
  for (const [x, fn, s] of items) fn(el('g', { transform: `translate(${x},${f1(ym(x) + 5)}) scale(${s})` }, g));
});

layer('road', 1, 3200, (g, T) => {
  el('rect', { x: -2, y: 744, width: T + 4, height: 70, style: S('road') }, g);
  el('rect', { x: -2, y: 744, width: T + 4, height: 3, style: S('roadLo'), opacity: .7 }, g);
  el('rect', { x: -2, y: 748, width: T + 4, height: 5, style: S('roadHi'), opacity: .8 }, g);
  for (const y of [770, 790]) el('rect', { x: -2, y, width: T + 4, height: 2, style: S('roadLo'), opacity: .35 }, g);
  for (let i = 0; i < 90; i++) {
    const x = R(5, T - 5), y = R(752, 808), rx = R(1.8, 5);
    el('ellipse', { cx: f1(x), cy: f1(y), rx: f1(rx), ry: f1(rx * R(.5, .7)), style: S('peb') }, g);
    if (rand() < .4) el('ellipse', { cx: f1(x - rx * .3), cy: f1(y - rx * .2), rx: f1(rx * .4), ry: f1(rx * .25), style: S('pebHi') }, g);
  }
  // 篱笆
  let rails = '';
  for (let x = 300; x <= 960; x += 44) el('rect', { x: x - 3, y: 700, width: 6, height: 48, rx: 2, style: S('wood') }, g);
  rails += 'M300,714 L960,714 M300,730 L960,730';
  el('path', { d: rails, style: 'fill:none;stroke:var(--wood2);stroke-width:4' }, g);
  // 路牌：桃花源
  el('rect', { x: 1921, y: 668, width: 7, height: 80, style: S('wood2') }, g);
  el('path', { d: 'M1858,664 L1978,664 L1994,679 L1978,694 L1858,694 Z', style: 'fill:var(--wood);stroke:var(--wood2);stroke-width:2' }, g);
  el('text', { x: 1920, y: 685, 'text-anchor': 'middle', 'font-size': 16, 'font-weight': 700, 'font-family': 'STKaiti, KaiTi, 楷体, serif', style: S('wood2') }, g).textContent = '桃花源 · 三里';
  // 路边草丛与石头
  let tufts = '';
  for (let i = 0; i < 40; i++) { const x = f1(R(10, T - 10)); tufts += `M${x},747 l-4,-10 M${x},747 l0,-13 M${x},747 l4,-10 `; }
  el('path', { d: tufts, style: 'fill:none;stroke:var(--grass2);stroke-width:2;stroke-linecap:round' }, g);
  for (const x of [1300, 2450, 2900]) el('ellipse', { cx: x, cy: 746, rx: 16, ry: 9, style: S('stone') }, g);
});

layer('front', 1.35, 1600, (g, T) => {
  const yg = x => 814 + 3 * Math.sin(TAU * 4 * x / T) + 2 * Math.sin(TAU * 9 * x / T + 2);
  el('path', { d: curve(yg, T, 905), style: S('ground') }, g);
  for (let c = 0; c < 20; c++) {
    const cx = c * T / 20 + R(0, 6);
    const cg = el('g', { class: 'sway', style: `animation-delay:${f1(-R(0, 3.4))}s;animation-duration:${f1(R(2.6, 4))}s` }, g);
    let d1 = '', d2 = '';
    for (let i = 0; i < 18; i++) {
      const x = cx + R(0, 74), b = yg(x) + 4, h = R(14, 40), ln = R(-8, 8);
      const bl = `M${f1(x - 2.4)},${f1(b)} Q${f1(x + ln * .3)},${f1(b - h * .6)} ${f1(x + ln)},${f1(b - h)} Q${f1(x + ln * .3 + 1.2)},${f1(b - h * .5)} ${f1(x + 2.4)},${f1(b)} Z `;
      if (i % 2) d1 += bl; else d2 += bl;
    }
    el('path', { d: d1, style: S('grass2') }, cg);
    el('path', { d: d2, style: S('grass') }, cg);
    for (let k = 0; k < 2; k++) { const x = cx + R(5, 70); flower(cg, x, yg(x) + 4); }
  }
});

/* ---------------- 天空元素 ---------------- */
const starsG = $('stars');
el('ellipse', { cx: 900, cy: 200, rx: 720, ry: 70, transform: 'rotate(-16 900 200)', fill: 'url(#gMilky)' }, starsG);
for (let i = 0; i < 150; i++)
  el('circle', { cx: f1(R(0, 1600)), cy: f1(Math.pow(rand(), 1.4) * 470), r: f1(R(.5, 1.7)), fill: '#fff', class: 'tw', style: `animation-delay:${f1(-R(0, 3))}s;animation-duration:${f1(R(2, 5))}s` }, starsG);

const clouds = [];
for (let i = 0; i < 7; i++) {
  const g = el('g', null, $('clouds'));
  const puffs = [[-48, 0, 26], [-14, -20, 34], [28, -26, 38], [66, -6, 27], [92, 6, 18]].map(([x, y, r]) => [x + R(-6, 6), y + R(-5, 5), r * R(.85, 1.15)]);
  for (const [dx, dy, cls] of [[5, 8, 'cloud2'], [0, 0, 'cloud']]) {
    const sg = el('g', { transform: `translate(${dx},${dy})`, style: S(cls) }, g);
    for (const [x, y, r] of puffs) el('circle', { cx: f1(x), cy: f1(y), r: f1(r) }, sg);
    el('rect', { x: -74, y: -4, width: 186, height: 26, rx: 13 }, sg);
  }
  clouds.push({ g, x: R(-200, 1700), y: R(90, 330), s: R(.55, 1.3), v: R(5, 13) });
}

const flock = { on: false, x: 0, y: 0, wait: 4, birds: [] };
for (let i = 0; i < 6; i++) flock.birds.push(el('path', { style: 'fill:none;stroke:var(--egret);stroke-width:2.4;stroke-linecap:round;stroke-linejoin:round' }, $('flock')));

/* ---------------- 骑手部件 ---------------- */
for (const id of ['spokesR', 'spokesF']) {
  const g = $(id); let d = '';
  for (let i = 0; i < 16; i++) {
    const a = i * TAU / 16;
    d += `M${f1(5 * Math.cos(a))},${f1(5 * Math.sin(a))} L${f1(51 * Math.cos(a + .25))},${f1(51 * Math.sin(a + .25))} `;
  }
  el('path', { d, class: 'spoke' }, g);
  el('rect', { x: 30, y: -2.5, width: 9, height: 5, rx: 1.5, fill: '#ff9d2e' }, g);
  if (id === 'spokesR') el('circle', { r: 10, class: 'cog' }, g);
}
// 鹧鸪身上的白色斑点
for (let y = -272; y < -140; y += 11)
  for (let x = -22; x < 125; x += 13) {
    const big = y > -215 ? 1.25 : .9;
    el('ellipse', { cx: f1(x + R(-3, 3) + (y / 11 % 2) * 6), cy: f1(y + R(-2, 2)), rx: f1(2.8 * big), ry: f1(2.2 * big), class: 'spotf' }, $('spots'));
  }
for (let y = -238; y < -165; y += 9)
  for (let x = 44; x < 150; x += 12)
    el('circle', { cx: f1(x + R(-2, 2) + (y / 9 % 2) * 5), cy: f1(y), r: 1.7, class: 'wspotf' }, $('wspots'));

const RX = 640, RY = 774, BASEV = 250, CYCLE = 110, LEG = 70;
const rid = {};
['spokesR', 'spokesF', 'ring', 'chain', 'crankN', 'crankF', 'pedalN', 'pedalF', 'thighN', 'thighF', 'shinN', 'shinF', 'toesN', 'toesF',
  'upper', 'head', 'eye', 'chick', 'scarfA', 'scarfB', 'beam', 'lampGlow', 'shadow', 'bubble', 'bubbleText', 'bell'].forEach(k => { rid[k] = $(k); });

function taper(x1, y1, x2, y2, w1, w2) {
  const dx = x2 - x1, dy = y2 - y1, L = Math.hypot(dx, dy) || 1, nx = -dy / L, ny = dx / L, a = w1 / 2, b = w2 / 2;
  return `M${f1(x1 + nx * a)},${f1(y1 + ny * a)} L${f1(x2 + nx * b)},${f1(y2 + ny * b)} A${b},${b} 0 0 0 ${f1(x2 - nx * b)},${f1(y2 - ny * b)} ` +
    `L${f1(x1 - nx * a)},${f1(y1 - ny * a)} A${a},${a} 0 0 0 ${f1(x1 + nx * a)},${f1(y1 + ny * a)} Z`;
}
// 两段式反向运动学：髋 -> 膝 -> 脚踝（踩在踏板上）
function leg(hx, hy, px, py, thigh, shin, toes) {
  const ax = px - 3, ay = py - 5;
  let dx = ax - hx, dy = ay - hy, d = Math.hypot(dx, dy);
  const mx = LEG * 2 - 1;
  if (d > mx) { dx *= mx / d; dy *= mx / d; d = mx; }
  const a = Math.atan2(dy, dx), al = Math.acos(clamp(d / (2 * LEG), -1, 1));
  const kx = hx + LEG * Math.cos(a - al), ky = hy + LEG * Math.sin(a - al), ex = hx + dx, ey = hy + dy;
  thigh.setAttribute('d', taper(hx, hy, kx, ky, 22, 12));
  shin.setAttribute('x1', f1(kx)); shin.setAttribute('y1', f1(ky));
  shin.setAttribute('x2', f1(ex)); shin.setAttribute('y2', f1(ey));
  const p = `M${f1(ex)},${f1(ey)}`;
  toes.setAttribute('d', `${p} l13,4 ${p} l10,7.5 ${p} l-6,4`);
}
function setLine(e, x1, y1, x2, y2) {
  e.setAttribute('x1', f1(x1)); e.setAttribute('y1', f1(y1)); e.setAttribute('x2', f1(x2)); e.setAttribute('y2', f1(y2));
}
function scarf(e, len, amp, w, ph, sp) {
  const kx = 84, ky = -236, N = 14, top = [], bot = [];
  const droop = Math.max(0, 1 - sp);
  for (let i = 0; i <= N; i++) {
    const s = i / N;
    const x = kx - len * s * (.55 + .45 * sp);
    const y = ky + 3 * s + droop * 34 * s * s + amp * s * Math.sin(time * (6 + 6 * sp) - s * 5.5 + ph);
    const ww = w * (1 - .55 * s) / 2;
    top.push(`${f1(x)},${f1(y - ww)}`); bot.push(`${f1(x)},${f1(y + ww)}`);
  }
  e.setAttribute('d', 'M' + top.join(' L') + ' L' + bot.reverse().join(' L') + ' Z');
}

/* ---------------- 粒子：花瓣 / 萤火虫 / 尘土 / 蝴蝶 ---------------- */
const fx = $('fx');
const petalG = el('g', null, fx), flyG = el('g', null, fx), bflyG = el('g', null, fx);
const petals = [];
for (let i = 0; i < 28; i++)
  petals.push({ e: el('path', { d: 'M0,-5 C4,-5 6,0 0,6 C-6,0 -4,-5 0,-5 Z', style: S(i % 3 ? 'bl1' : 'bl2') }, petalG),
    x: R(0, 1600), y: R(0, 880), vy: R(22, 45), r: R(0, 360), vr: R(-120, 120), ph: R(0, TAU), fl: R(0, TAU), fs: R(2, 5), s: R(.8, 1.4) });
const flies = [];
for (let i = 0; i < 26; i++) {
  const g = el('g', null, flyG);
  el('circle', { r: 8, fill: 'url(#gFly)' }, g);
  el('circle', { r: 1.6, fill: '#fffbd0' }, g);
  flies.push({ e: g, x: R(0, 1600), by: R(600, 870), ph: R(0, TAU) });
}
const dusts = [];
for (let i = 0; i < 16; i++) dusts.push({ e: el('circle', { r: 0, style: S('roadHi'), opacity: 0 }, $('dust')), age: 1 });
let dustT = 0;
// 蝴蝶
const bfly = el('g', null, bflyG);
const wingL = el('g', null, bfly), wingR = el('g', null, bfly);
for (const [w, sx] of [[wingL, -1], [wingR, 1]]) {
  el('ellipse', { cx: 7 * sx, cy: -4, rx: 8, ry: 6, fill: '#ffcf3d', stroke: '#8a5a12', 'stroke-width': 1 }, w);
  el('ellipse', { cx: 5 * sx, cy: 5, rx: 5, ry: 4.5, fill: '#ffa53d', stroke: '#8a5a12', 'stroke-width': 1 }, w);
}
el('ellipse', { cx: 0, cy: 0, rx: 1.6, ry: 7, fill: '#3b2a1a' }, bfly);
const shoot = { e: $('shoot'), on: false, t: 0, x: 0, y: 0, a: 0 };

/* ---------------- 状态 ---------------- */
let time = 0, dist = 0, v = BASEV, tod = 0.30, playing = true, autoTime = true, speedMul = 1;
let palT = 1, blinkT = 3, blinkOn = 0, talkT = 18, last = performance.now();
const sunG = $('sun'), sunGlow = $('sunGlow'), moonG = $('moon');

function updateSky() {
  let sx = -999, sy = 999;
  const sunOn = tod < .62;
  if (sunOn) { const s = tod / .62; sx = 150 + 1300 * s; sy = 620 - 470 * Math.sin(Math.PI * s); }
  sunG.setAttribute('transform', `translate(${f1(sx)},${f1(sy)})`);
  const low = sunOn ? clamp((sy - 250) / 320, 0, 1) : 0;
  sunGlow.setAttribute('r', (230 * (1 + .6 * low)).toFixed(0));
  let mx = -999, my = 999, mo = 0;
  if (tod > .58) { const s = (tod - .58) / .42; mx = 200 + 1200 * s; my = 620 - 450 * Math.sin(Math.PI * s); mo = clamp(Math.min((tod - .6) / .05, (1 - tod) / .04), 0, 1); }
  moonG.setAttribute('transform', `translate(${f1(mx)},${f1(my)})`);
  moonG.style.opacity = mo;
  starsG.style.opacity = smooth(.3, .9, P.night);
  // 水面上的日 / 月光带
  if (sunOn && P.night < .6) {
    glitter.setAttribute('transform', `translate(${f1(sx)},0)`);
    glitter.style.fill = rgb(P.sun);
    glitter.style.opacity = ((1 - P.night) * clamp((sy - 260) / 280, 0, 1) * .9).toFixed(2);
  } else {
    glitter.setAttribute('transform', `translate(${f1(mx)},0)`);
    glitter.style.fill = '#e8eeff';
    glitter.style.opacity = (P.night * mo * .7).toFixed(2);
  }
  const n = P.night;
  rid.beam.style.opacity = smooth(.35, .85, n).toFixed(2);
  rid.lampGlow.style.opacity = smooth(.3, .8, n).toFixed(2);
  rid.shadow.setAttribute('opacity', (.22 * (1 - .6 * n)).toFixed(3));
  petalG.style.opacity = (1 - .6 * n).toFixed(2);
  flyG.style.opacity = smooth(.4, .9, n).toFixed(2);
  bflyG.style.opacity = clamp(1 - n * 1.6, 0, 1).toFixed(2);
}

function updateRider(dt) {
  const wa = dist / 60;
  const wdeg = (wa * 180 / Math.PI) % 360;
  rid.spokesR.setAttribute('transform', `rotate(${wdeg.toFixed(2)})`);
  rid.spokesF.setAttribute('transform', `rotate(${wdeg.toFixed(2)})`);
  const th = wa / 1.6;
  rid.ring.setAttribute('transform', `rotate(${((th * 180 / Math.PI) % 360).toFixed(2)})`);
  rid.chain.setAttribute('stroke-dashoffset', (-(th * 16) % 50).toFixed(2));
  const cx = 82, cy = -52, L = 26, c = Math.cos(th), s = Math.sin(th);
  const nx = cx + L * c, ny = cy + L * s, fx2 = cx - L * c, fy2 = cy - L * s;
  setLine(rid.crankN, cx, cy, nx, ny); setLine(rid.crankF, cx, cy, fx2, fy2);
  rid.pedalN.setAttribute('x', f1(nx - 8)); rid.pedalN.setAttribute('y', f1(ny - 2.5));
  rid.pedalF.setAttribute('x', f1(fx2 - 8)); rid.pedalF.setAttribute('y', f1(fy2 - 2.5));
  const bob = 1.6 * Math.sin(th * 2);
  rid.upper.setAttribute('transform', `translate(0,${f1(bob)})`);
  leg(58, -162 + bob, nx, ny, rid.thighN, rid.shinN, rid.toesN);
  leg(50, -162 + bob, fx2, fy2, rid.thighF, rid.shinF, rid.toesF);
  rid.head.setAttribute('transform', `rotate(${f1(2.2 * Math.sin(th * 2 + .8))} 100 -248)`);
  rid.chick.setAttribute('transform', `translate(0,${f1(1.5 * Math.sin(th * 4))})`);
  const sp = clamp(v / BASEV, 0, 1.3);
  scarf(rid.scarfA, 110, 9, 13, 0, sp);
  scarf(rid.scarfB, 84, 7, 10, 1.3, sp);
  // 眨眼
  blinkT -= dt;
  if (blinkT < 0) { blinkOn = .14; blinkT = R(2.5, 5.5); }
  let ey = 1;
  if (blinkOn > 0) { blinkOn -= dt; ey = .12; }
  rid.eye.setAttribute('transform', `translate(118 -268) scale(1 ${ey}) translate(-118 268)`);
}

function updateParticles(dt) {
  const n = P.night;
  for (const p of petals) {
    p.x += (-(v * .55) - 25 + Math.sin(time * .8 + p.ph) * 15) * dt;
    p.y += (p.vy + Math.sin(time * 2 + p.ph) * 12) * dt;
    p.r += p.vr * dt; p.fl += p.fs * dt;
    if (p.x < -30 || p.y > 910) {
      if (rand() < .5) { p.x = R(1610, 1700); p.y = R(0, 600); } else { p.x = R(300, 1750); p.y = R(-40, -10); }
    }
    p.e.setAttribute('transform', `translate(${f1(p.x)},${f1(p.y)}) rotate(${f1(p.r % 360)}) scale(${(Math.cos(p.fl) * p.s).toFixed(2)},${p.s.toFixed(2)})`);
  }
  if (n > .05) for (const f of flies) {
    f.x -= v * .35 * dt;
    if (f.x < -20) f.x += 1640;
    const x = f.x + Math.sin(time * .9 + f.ph * 2) * 20, y = f.by + Math.sin(time * .7 + f.ph) * 18;
    f.e.setAttribute('transform', `translate(${f1(x)},${f1(y)})`);
    f.e.setAttribute('opacity', (.2 + .8 * Math.max(0, Math.sin(time * 1.7 + f.ph))).toFixed(2));
  }
  // 后轮扬起的尘土
  dustT -= dt;
  if (v > 60 && dustT < 0) {
    dustT = .1 / clamp(v / BASEV, .4, 2.5);
    const d = dusts.find(q => q.age >= 1);
    if (d) { d.age = 0; d.x = RX + R(-12, 8); d.y = RY - R(1, 5); }
  }
  for (const d of dusts) {
    if (d.age >= 1) continue;
    d.age += dt / .9; d.x -= v * .9 * dt; d.y -= 14 * dt;
    d.e.setAttribute('cx', f1(d.x)); d.e.setAttribute('cy', f1(d.y));
    d.e.setAttribute('r', f1(2 + 9 * d.age));
    d.e.setAttribute('opacity', d.age >= 1 ? 0 : (.35 * (1 - d.age)).toFixed(2));
  }
  // 蝴蝶围着车篮飞
  if (n < .6) {
    const bx = RX + 290 + 50 * Math.sin(time * .6), by = RY - 255 + 30 * Math.sin(time * 1.1) + 8 * Math.sin(time * 5);
    bfly.setAttribute('transform', `translate(${f1(bx)},${f1(by)}) rotate(${f1(15 * Math.sin(time * .9))})`);
    const k = (.25 + .75 * Math.abs(Math.sin(time * 14))).toFixed(2);
    wingL.setAttribute('transform', `scale(${k},1)`); wingR.setAttribute('transform', `scale(${k},1)`);
  }
}

function updateSkyLife(dt) {
  for (const c of clouds) {
    c.x -= (c.v + v * .02) * dt;
    if (c.x < -260 * c.s) { c.x = 1750 + R(0, 300); c.y = R(90, 330); }
    c.g.setAttribute('transform', `translate(${f1(c.x)},${f1(c.y)}) scale(${c.s.toFixed(2)})`);
  }
  // 一行白鹭上青天
  if (!flock.on) {
    flock.wait -= dt;
    if (flock.wait < 0 && P.night < .15) { flock.on = true; flock.x = -150; flock.y = R(380, 460); }
    flock.birds.forEach(b => b.setAttribute('d', ''));
  } else {
    flock.x += 55 * dt; flock.y -= 14 * dt;
    flock.birds.forEach((b, i) => {
      const sn = Math.sin(time * 7 + i * .7), x = flock.x - i * 34, y = flock.y + i * 13;
      b.setAttribute('d', `M${f1(x - 15)},${f1(y - 7 * sn)} Q${f1(x - 7)},${f1(y - 2 - 5 * sn)} ${f1(x)},${f1(y)} Q${f1(x + 7)},${f1(y - 2 - 5 * sn)} ${f1(x + 15)},${f1(y - 7 * sn)}`);
    });
    if (flock.x - 6 * 34 > 1700) { flock.on = false; flock.wait = R(12, 25); }
  }
  // 流星
  if (!shoot.on && P.night > .8 && rand() < dt * .15) {
    shoot.on = true; shoot.t = 0; shoot.x = R(500, 1500); shoot.y = R(30, 200); shoot.a = R(150, 165);
  }
  if (shoot.on) {
    shoot.t += dt;
    const rad = shoot.a * Math.PI / 180, dd = shoot.t * 900;
    shoot.e.setAttribute('transform', `translate(${f1(shoot.x + Math.cos(rad) * dd)},${f1(shoot.y + Math.sin(rad) * dd)}) rotate(${shoot.a})`);
    shoot.e.setAttribute('opacity', Math.max(0, 1 - shoot.t / .7).toFixed(2));
    if (shoot.t > .7) shoot.on = false;
  }
}

/* ---------------- 文字与声音 ---------------- */
const PERIODS = [[.05, '黎明', 0], [.14, '清晨', 1], [.26, '上午', 2], [.36, '正午', 2], [.48, '午后', 3], [.57, '黄昏', 4],
  [.63, '日落', 4], [.70, '薄暮', 5], [.93, '夜晚', 6], [1.01, '破晓', 0]];
const POEMS = [['春眠不觉晓，处处闻啼鸟。', '孟浩然《春晓》'], ['日出江花红胜火，春来江水绿如蓝。', '白居易《忆江南》'],
  ['两个黄鹂鸣翠柳，一行白鹭上青天。', '杜甫《绝句》'], ['暖戏烟芜锦翼齐，品流应得近山鸡。', '郑谷《鹧鸪》'],
  ['落霞与孤鹜齐飞，秋水共长天一色。', '王勃《滕王阁序》'], ['江晚正愁余，山深闻鹧鸪。', '辛弃疾《菩萨蛮》'],
  ['明月别枝惊鹊，清风半夜鸣蝉。', '辛弃疾《西江月》']];
const poem = $('poem'), poemText = $('poemText'), poemBy = $('poemBy'), todOut = $('todOut'), todIn = $('tod');
let lastPoem = -1, poemTimer = 0;
function updateLabels() {
  let i = 0;
  while (tod >= PERIODS[i][0]) i++;
  todOut.textContent = PERIODS[i][1];
  const pi = PERIODS[i][2];
  if (pi === lastPoem) return;
  const first = lastPoem < 0;
  lastPoem = pi;
  const set = () => { poemText.textContent = '「' + POEMS[pi][0] + '」'; poemBy.textContent = '—— ' + POEMS[pi][1]; poem.classList.remove('fade'); };
  if (first) set(); else { poem.classList.add('fade'); clearTimeout(poemTimer); poemTimer = setTimeout(set, 600); }
}

let actx = null;
function audio() {
  if (!actx) { const C = window.AudioContext || window.webkitAudioContext; if (!C) return null; actx = new C(); }
  if (actx.state === 'suspended') actx.resume();
  return actx;
}
function tone(a, f0, f1v, start, dur, peak) {
  const o = a.createOscillator(), g = a.createGain();
  o.type = 'sine';
  o.frequency.setValueAtTime(f0, start);
  if (f1v !== f0) o.frequency.exponentialRampToValueAtTime(f1v, start + dur * .6);
  g.gain.setValueAtTime(.0001, start);
  g.gain.exponentialRampToValueAtTime(peak, start + .008);
  g.gain.exponentialRampToValueAtTime(.0001, start + dur);
  o.connect(g); g.connect(a.destination);
  o.start(start); o.stop(start + dur + .05);
}
function ringBell() {
  rid.bell.classList.remove('ring');
  requestAnimationFrame(() => rid.bell.classList.add('ring'));
  const a = audio(); if (!a) return;
  const t = a.currentTime;
  for (const o of [0, .18]) { tone(a, 2100, 2100, t + o, 1.1, .16); tone(a, 2650, 2650, t + o, .9, .08); tone(a, 4200, 4200, t + o, .5, .03); }
}
function chirp() {
  const a = audio(); if (!a) return;
  const t = a.currentTime;
  [[1500, 2300], [1700, 2500], [1300, 2000]].forEach(([x, y], i) => tone(a, x, y, t + i * .17, .14, .12));
}
const MSG = ['行不得也哥哥～', '咕咕！风景真好', '宝宝坐稳啦', '桃花开得正好', '前面就是桃花源'];
let hideT = 0;
function talk(sound) {
  let msg = MSG[Math.floor(rand() * MSG.length)];
  if (P.night > .6) msg = rand() < .5 ? '夜路开着灯呢' : '月亮好圆呀';
  rid.bubbleText.textContent = msg;
  rid.bubble.classList.add('show');
  clearTimeout(hideT);
  hideT = setTimeout(() => rid.bubble.classList.remove('show'), 2800);
  if (sound) chirp();
}

/* ---------------- 主循环 ---------------- */
function frame(now) {
  const dt = Math.min(.05, (now - last) / 1000);
  last = now;
  if (playing) {
    time += dt;
    v += (BASEV * speedMul - v) * (1 - Math.exp(-dt * 2.5));
    dist += v * dt;
    if (autoTime) tod = (tod + dt / CYCLE) % 1;
    palT += dt;
    if (palT > .1) {
      palT = 0; applyPalette(); updateLabels();
      if (autoTime && document.activeElement !== todIn) todIn.value = Math.round(tod * 1000) % 1000;
    }
    for (const Ly of layers) Ly.g.setAttribute('transform', `translate(${(-((dist * Ly.speed) % Ly.tile)).toFixed(2)},0)`);
    updateSky();
    updateRider(dt);
    updateParticles(dt);
    updateSkyLife(dt);
    talkT -= dt;
    if (talkT < 0) { talk(false); talkT = R(22, 34); }
  }
  requestAnimationFrame(frame);
}

/* ---------------- 交互 ---------------- */
const btnPlay = $('btnPlay'), btnAuto = $('btnAuto'), speedIn = $('speed'), speedOut = $('speedOut');
function setPlaying(p) {
  playing = p;
  btnPlay.textContent = p ? '❚❚ 暂停' : '▶ 继续';
  btnPlay.setAttribute('aria-label', p ? '暂停动画' : '继续动画');
  svg.classList.toggle('paused', !p);
}
function setSpeed(x) {
  speedMul = clamp(Math.round(x * 10) / 10, .2, 2.5);
  speedIn.value = speedMul;
  speedOut.textContent = speedMul.toFixed(1) + '×';
}
btnPlay.addEventListener('click', () => setPlaying(!playing));
$('btnBell').addEventListener('click', ringBell);
btnAuto.addEventListener('click', () => { autoTime = !autoTime; btnAuto.setAttribute('aria-pressed', String(autoTime)); });
speedIn.addEventListener('input', () => setSpeed(+speedIn.value));
todIn.addEventListener('input', () => { tod = +todIn.value / 1000; applyPalette(); updateSky(); updateLabels(); });
const rider = $('rider');
rider.addEventListener('click', () => talk(true));
rider.addEventListener('keydown', e => { if (e.key === 'Enter') { e.preventDefault(); talk(true); } });
window.addEventListener('keydown', e => {
  if (e.target instanceof HTMLInputElement) return;
  if (e.code === 'Space') { e.preventDefault(); setPlaying(!playing); }
  else if (e.key === 'b' || e.key === 'B') ringBell();
  else if (e.key === 'ArrowRight') setSpeed(speedMul + .1);
  else if (e.key === 'ArrowLeft') setSpeed(speedMul - .1);
});

applyPalette();
updateLabels();
for (const Ly of layers) Ly.g.setAttribute('transform', 'translate(0,0)');
updateSky();
updateRider(0);
requestAnimationFrame(frame);
