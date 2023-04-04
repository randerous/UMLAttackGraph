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
### test
ATM实例的UML图

### AttackPathGenerator
连通图生成代码，路径生成代码，新增了类成员提取接口
数据结构修改
邻接表存储图，新增了前驱节点信息
```
public class Vertex {
    int type; // 0: surface, 1: Node, 2: Asset
    Node itself;// keep current Node info
    Set<Vertex> next_vertexes;
    Set<Vertex> pre_vertexes;
    
```


### adjustmentWork
架构优化算法

### controlNodeWork
隔离域操作

### CriticalNodesGenerator
控制点生成算法

### EvaluationWork
架构评估

### StartOver
启动函数

### Visualization
图可视化

### UMLgenerator
uml样例生成算法


