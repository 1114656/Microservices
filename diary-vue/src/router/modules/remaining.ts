import { Layout } from '@/utils/routerHelper'

const { t } = useI18n()

const remainingRouter: AppRouteRecordRaw[] = [
  {
    path: '/redirect',
    component: Layout,
    name: 'Redirect',
    children: [
      {
        path: '/redirect/:path(.*)',
        name: 'Redirect',
        component: () => import('@/views/Redirect/Redirect.vue'),
        meta: {}
      }
    ],
    meta: {
      hidden: true,
      noTagsView: true
    }
  },
  {
    path: '/',
    component: () => import('@/views/site/Home.vue'),
    name: 'SiteHome',
    meta: {
      hidden: true,
      title: 'Diary',
      noTagsView: true
    }
  },
  {
    path: '/diary',
    component: () => import('@/views/site/diary/index.vue'),
    name: 'SiteDiary',
    meta: {
      hidden: true,
      title: 'Diary',
      noTagsView: true
    }
  },
  {
    path: '/blog',
    component: () => import('@/views/site/blog/index.vue'),
    name: 'SiteBlog',
    meta: {
      hidden: true,
      title: 'Blog',
      noTagsView: true
    }
  },
  {
    path: '/index',
    component: Layout,
    name: 'Home',
    meta: {},
    children: [
      {
        path: '',
        component: () => import('@/views/Home/Index.vue'),
        name: 'Index',
        meta: {
          title: t('router.home'),
          icon: 'ep:home-filled',
          noCache: false,
          affix: true
        }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    name: 'UserInfo',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'profile',
        component: () => import('@/views/Profile/Index.vue'),
        name: 'Profile',
        meta: {
          canTo: true,
          hidden: true,
          noTagsView: false,
          icon: 'ep:user',
          title: t('common.profile')
        }
      }
    ]
  },
  {
    path: '/content/diary/create',
    component: Layout,
    name: 'DiaryEntryCreateParent',
    meta: {
      hidden: true
    },
    children: [
      {
        path: '',
        component: () => import('@/views/diary/entry/DiaryEntryEditor.vue'),
        name: 'DiaryEntryCreate',
        meta: {
          title: '新增日记',
          hidden: true,
          noCache: true
        }
      }
    ]
  },
  {
    path: '/content/diary/edit',
    component: Layout,
    name: 'DiaryEntryEditParent',
    meta: {
      hidden: true
    },
    children: [
      {
        path: '',
        component: () => import('@/views/diary/entry/DiaryEntryEditor.vue'),
        name: 'DiaryEntryEdit',
        meta: {
          title: '编辑日记',
          hidden: true,
          noCache: true
        }
      }
    ]
  },
  {
    path: '/content/blog/create',
    component: Layout,
    name: 'BlogArticleCreateParent',
    meta: {
      hidden: true
    },
    children: [
      {
        path: '',
        component: () => import('@/views/blog/article/BlogArticleEditor.vue'),
        name: 'BlogArticleCreate',
        meta: {
          title: '新增博客',
          hidden: true,
          noCache: true
        }
      }
    ]
  },
  {
    path: '/content/blog/edit',
    component: Layout,
    name: 'BlogArticleEditParent',
    meta: {
      hidden: true
    },
    children: [
      {
        path: '',
        component: () => import('@/views/blog/article/BlogArticleEditor.vue'),
        name: 'BlogArticleEdit',
        meta: {
          title: '编辑博客',
          hidden: true,
          noCache: true
        }
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/Login/Login.vue'),
    name: 'Login',
    meta: {
      hidden: true,
      title: t('router.login'),
      noTagsView: true
    }
  },
  {
    path: '/403',
    component: () => import('@/views/Error/403.vue'),
    name: 'NoAccess',
    meta: {
      hidden: true,
      title: '403',
      noTagsView: true
    }
  },
  {
    path: '/404',
    component: () => import('@/views/Error/404.vue'),
    name: 'NoFound',
    meta: {
      hidden: true,
      title: '404',
      noTagsView: true
    }
  },
  {
    path: '/500',
    component: () => import('@/views/Error/500.vue'),
    name: 'Error',
    meta: {
      hidden: true,
      title: '500',
      noTagsView: true
    }
  },
  {
    path: '/:pathMatch(.*)*',
    component: () => import('@/views/Error/404.vue'),
    name: '',
    meta: {
      title: '404',
      hidden: true,
      breadcrumb: false
    }
  }
]

export default remainingRouter
