

import { Button, Flex, Form, Image, Input, Typography, type FormProps } from "antd";
import { Link } from "react-router";
import type { RegisterDto } from "../../types/auth";
import { register } from "@api/auth";
import openHttpNotifation from "@utils/httpNotifaction";

const { Title, Text } = Typography;

export default function Register() {

    const onSubmit: FormProps<RegisterDto>['onFinish'] = async (values) => {
        console.log('Success:', values);
        const res = await register(values)
        openHttpNotifation(res)
            .then((res) => {
                console.log(res);
            })
            .catch((err) => {
                console.log(err);

            })

    };


    return (
        <Form<RegisterDto>
            name="formData"
            autoComplete="off"
            layout="vertical"
            size="large"
            onFinish={onSubmit}
            style={{
                width: "100%",
                maxWidth: "420px",
                backgroundColor: "#fff",
                padding: "40px 36px",
                borderRadius: "12px",
                boxShadow: "0 12px 40px rgba(0, 0, 0, 0.2)"
            }}
        >
            <Flex vertical align="center" gap={4} style={{ marginBottom: 32 }}>
                <Image
                    preview={false}
                    src="/apple.svg"
                    width={64}
                    style={{ marginBottom: 8 }}
                />
                <Title level={3} style={{ marginBottom: 0 }}>
                    欢迎登录 ~
                </Title>
                <Text type="secondary">LZMall 商城管理平台</Text>
            </Flex>

            <Form.Item<RegisterDto>
                label="用户名"
                name="username"
                rules={[{ required: true, message: "请输入用户名" }]}
            >
                <Input placeholder="请输入用户名" />
            </Form.Item>

            <Form.Item<RegisterDto>
                label="密码"
                name="password"
                rules={[{ required: true, message: "请输入密码" }]}
            >
                <Input.Password placeholder="请输入密码" />
            </Form.Item>
            <Form.Item<RegisterDto>
                label="确认密码"
                name="validatePassword"
                rules={[{ required: true, message: "请输入密码" }]}
            >
                <Input.Password placeholder="请输入密码" />
            </Form.Item>

            {/* <Flex justify="space-between" align="center" style={{ marginBottom: 24 }}>
                <Checkbox>记住我</Checkbox>
                <Link to="/auth" className="text-sm">
                    忘记密码？
                </Link>
            </Flex> */}

            <Form.Item style={{ marginBottom: 16 }}>
                <Button htmlType="submit" type="primary" block>
                    注册
                </Button>
            </Form.Item>

            <Flex justify="center" gap={4}>
                <Text type="secondary">已有账号？</Text>
                <Link to="/auth/login" className="text-sm">
                    去登录
                </Link>
            </Flex>
        </Form>
    );
}
