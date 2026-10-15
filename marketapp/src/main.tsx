import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { RouterProvider } from 'react-router'
import { router } from './router'

import dayjs from 'dayjs'
dayjs.locale('zh-cn')

// 配置antd
import { ConfigProvider } from "antd"
import 'dayjs/locale/zh-cn';
import zhCN from 'antd/locale/zh_CN';

import './assets/style/main.css'



createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ConfigProvider locale={zhCN}>
      <RouterProvider router={router} />
    </ConfigProvider>
  </StrictMode>,
)
