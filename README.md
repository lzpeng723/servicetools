# 服务工具平台

包含类加载查看和动态执行代码功能

## 导入应用：

- 从 MC 或 开发平台导入 ```src/main/resources/metadata/lzp_test-lzp_service_helper.zip```

## 部署代码：

- 生成zip包
  执行 ```src/test/java/PackageTest``` 的 ```genCustomZip``` 和 ```genServiceToolsZip``` 生成 zip 包
- 将 zip 包部署到服务器

## 初始化数据

- 若导入应用时 sql 未执行成功可自行执行一下 sql ：

```
# secd 库下执行
src/main/resources/metadata/servicetools.sql
# sys库下执行
src/main/resources/metadata/permission.sql
```
