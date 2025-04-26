<div align=center>
<h1>Nattalie</h1>
<h4>基于 Minecraft 的 Minecraft Mod Coder Pack 的免费黑客客户端，支持的版本为1.12.2</h4>
By Pursue
</div>

## Dev QQ
Dev: 193923709

## QQ群
群号: 1034220345

## 声明
本项目已经完全免费，但是请遵守以下条例：
- 请勿使用该客户端去破坏其他不支持作弊的服务器.
- 请勿对该客户端进行二次更名并进行商用.

如果您非要违反上述条例，那么造成的后果完全由您自己承担！

## 其他
如果您修复了项目内的代码，您也可以将修改后的代码提交给我，如果审核通过了我会将您的代码加入到仓库内并且添加您的署名等.

我也会时不时提交一些新的代码

## 客户端内的验证
验证我并不移除，您可以手动移除或者改为您的验证库，我并不反对，毕竟客户端免费

如果您无能到连移除验证都不会，那么请看以下教程: 
- 1.找到net/minecraft/client目录内的 Minecraft.java 中的 startGame 的调用
- 2.并找到以下内容: 
- if (HWIDManager.checkKeyWithRemote(hwid)) {
  this.displayGuiScreen(new Setting());
  } else {
  System.out.println("验证并未通过！");
  System.out.println("YOU HWID-> " + hwid);
  StringSelection selection = new StringSelection(hwid);
  Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
  DebugHelper.displayTray("HWID验证", "错误，您的HWID并未通过，已为您复制好了", TrayIcon.MessageType.INFO);
  System.exit(-13);
  }
- 3.将它们删除后添加: 
- this.displayGuiScreen(new Setting());
- 4.跳转至Setting.java后
- 5.将 actionPerformed 这个调用内的 mc.displayGuiScreen(new Disclaimer()); 都修改为: 
- if (Minecraft.getMinecraft().serverName != null) {
  Minecraft.getMinecraft().displayGuiScreen(new GuiConnecting(new MainMenu(), Minecraft.getMinecraft(), Minecraft.getMinecraft().serverName, Minecraft.getMinecraft().serverPort));
  } else {
  Minecraft.getMinecraft().displayGuiScreen(new MainMenu());
  }

Setting的作用仅为设置您的客户端是否用模糊，如果您不想看到这个可以直接在步骤3时改为:

if (Minecraft.getMinecraft().serverName != null) {
Minecraft.getMinecraft().displayGuiScreen(new GuiConnecting(new MainMenu(), Minecraft.getMinecraft(), Minecraft.getMinecraft().serverName, Minecraft.getMinecraft().serverPort));
} else {
Minecraft.getMinecraft().displayGuiScreen(new MainMenu());
}

即可.