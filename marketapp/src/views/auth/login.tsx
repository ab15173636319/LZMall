import { Button, Checkbox, Flex, Form, Image, Input, notification, Typography, type FormProps } from "antd";
import { Link } from "react-router";
import type { LoginDto } from "../../types/auth";
import { login } from "@api/auth";
import openHttpNotifation from "@utils/httpNotifaction";

const { Title, Text } = Typography;

export default function Login() {

    const onSubmit: FormProps<LoginDto>['onFinish'] = async (values) => {
        const res = await login(values)
        openHttpNotifation<string>(res).then((res) => {
            console.log(res);
        }, (err) => {
            console.log(err);
        })
    };


    return (
        <Form<LoginDto>
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

            <Form.Item<LoginDto>
                label="用户名"
                name="username"
                rules={[{ required: true, message: "请输入用户名" }]}
            >
                <Input placeholder="请输入用户名" />
            </Form.Item>

            <Form.Item<LoginDto>
                label="密码"
                name="password"
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
                    登录
                </Button>
            </Form.Item>

            <Flex justify="center" gap={4}>
                <Text type="secondary">还没有账号？</Text>
                <Link to="/auth/register" className="text-sm">
                    立即注册
                </Link>
            </Flex>
        </Form>
    );
}
