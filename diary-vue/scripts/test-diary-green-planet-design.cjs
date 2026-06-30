const fs = require('node:fs')
const path = require('node:path')

const diaryPagePath = path.resolve(__dirname, '../src/views/site/diary/index.vue')
const diaryApiPath = path.resolve(__dirname, '../src/api/diary/entry/index.ts')
const axiosServicePath = path.resolve(__dirname, '../src/config/axios/service.ts')
const source = fs.readFileSync(diaryPagePath, 'utf8')
const diaryApiSource = fs.readFileSync(diaryApiPath, 'utf8')
const axiosServiceSource = fs.readFileSync(axiosServicePath, 'utf8')

const checks = [
  {
    name: 'uses the green planet hero asset',
    pass: source.includes('green-planet-hero.png')
  },
  {
    name: 'keeps the hero as a full viewport section',
    pass: /\.hero\s*{[\s\S]*min-height:\s*100(?:svh|vh)/.test(source)
  },
  {
    name: 'renders diary content after the fullscreen hero',
    pass:
      source.indexOf('class="hero"') !== -1 &&
      source.indexOf('class="content"') !== -1 &&
      source.indexOf('class="hero"') < source.indexOf('class="content"')
  },
  {
    name: 'uses a transparent navigation bar over the hero',
    pass:
      /\.site-header\s*{[\s\S]*background:\s*transparent/.test(source) &&
      source.includes('header-scrolled')
  },
  {
    name: 'includes a scroll cue before diary entries',
    pass: source.includes('scroll-cue') && source.includes('Scroll to diary')
  },
  {
    name: 'keeps the documentary metadata rail',
    pass: source.includes('observation-rail') && source.includes('Field Note')
  },
  {
    name: 'handles public diary API failures without an unhandled mounted-hook error',
    pass: /catch\s*{[\s\S]*list\.value\s*=\s*\[\]/.test(source)
  },
  {
    name: 'uses a silent public diary API request on the public homepage',
    pass:
      source.includes('getPublicDiaryPage') &&
      /getPublicDiaryPage[\s\S]*silentError:\s*true/.test(diaryApiSource)
  },
  {
    name: 'axios service suppresses global error UI for silent requests',
    pass:
      axiosServiceSource.includes('silentError') &&
      /if\s*\(!\s*isSilentError/.test(axiosServiceSource)
  },
  {
    name: 'uses a refined documentary empty state instead of the default admin empty state',
    pass: source.includes('empty-state') && source.includes('等待第一篇公开日记')
  },
  {
    name: 'animates hero copy, actions, and observation rail like documentary title cards',
    pass:
      source.includes('title-rise') &&
      source.includes('hero-title') &&
      source.includes('animation-delay')
  },
  {
    name: 'adds scroll-linked parallax to the real hero image asset',
    pass:
      source.includes('hero-backdrop') &&
      source.includes('heroBackdropStyle') &&
      source.includes('--hero-shift')
  },
  {
    name: 'reveals the diary content section after scrolling into view',
    pass:
      source.includes('content-visible') &&
      source.includes('IntersectionObserver') &&
      source.includes('isContentVisible')
  },
  {
    name: 'respects reduced motion preferences',
    pass: source.includes('@media (prefers-reduced-motion: reduce)')
  },
  {
    name: 'adds reversible side green-planet bloom layers to the diary content area',
    pass:
      source.includes('content-bloom left') &&
      source.includes('content-bloom right') &&
      source.includes('green-planet-hero.png')
  },
  {
    name: 'binds side flower emergence to scroll progress instead of one-shot visibility',
    pass:
      source.includes('contentDecorStyle') &&
      source.includes('--bloom-progress') &&
      source.includes('bloomProgress')
  },
  {
    name: 'keeps side bloom layers behind diary content and away from interaction targets',
    pass:
      source.includes('pointer-events: none') &&
      /\.content-bloom[\s\S]*z-index:\s*0/.test(source) &&
      /\.section-title,[\s\S]*z-index:\s*1/.test(source)
  }
]

const failures = checks.filter((check) => !check.pass)

if (failures.length > 0) {
  console.error('Diary green planet design checks failed:')
  for (const failure of failures) {
    console.error(`- ${failure.name}`)
  }
  process.exit(1)
}

console.log(`Diary green planet design checks passed (${checks.length}/${checks.length}).`)
