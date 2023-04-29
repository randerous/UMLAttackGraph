# UMLGraph
## preface
### 环境配置
#### 1 papyrus，直接搜官网可下载
#### 2 jdk 17
我装的这个
#### 3 为papyrus安装支持uml识别的插件
教程见 https://wiki.eclipse.org/MDT/UML2/Getting_Started_with_UML2
#### 4 需要导入一个额外的jar包用于识别JSON
fastjson-1.2.78.jar，右键项目AttackPathGenerator->properties->Java Build Path ->Libraries->Add External JARS
#### 5 安装win10上的graphviz用于展示图
勾选添加到环境路径，stable_windows_10_cmake_Release_x64_graphviz-install-2.49.3-win64

## 项目结构
### 输入
#### test
实例的UML图 以及 漏洞、暴露面、资产的标注
#### configJson
标注的安全信息，包括访问策略，架构中的用户证书，攻击者能力

### 处理
#### contextBuilder
解析json配置，构建访问控制、攻击者模型、漏洞模型、连通图模型
#### genGraphWithContext
攻击图生成和可视化

### AttackPathGenerator
攻击路径生成



