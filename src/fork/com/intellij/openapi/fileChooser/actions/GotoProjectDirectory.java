/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fork.com.intellij.openapi.fileChooser.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ex.ApplicationInfoEx;
import com.intellij.openapi.diagnostic.Logger;
import fork.com.intellij.openapi.fileChooser.FileSystemTree;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public final class GotoProjectDirectory extends FileChooserAction {
  private static final Logger LOG = Logger.getInstance("#com.intellij.openapi.fileChooser.actions.GotoProjectDirectory");

  private static final Icon ourIcon = IconLoader.getIcon(ApplicationInfoEx.getInstanceEx().getSmallIconUrl());

  protected void actionPerformed(final FileSystemTree fileSystemTree, AnActionEvent e) {
    final VirtualFile projectPath = getProjectPath(e);
    LOG.assertTrue(projectPath != null);
    fileSystemTree.select(projectPath, new Runnable() {
      public void run() {
        fileSystemTree.expand(projectPath, null);
      }
    });
  }

  protected void update(FileSystemTree fileSystemTree, AnActionEvent e) {
    final Presentation presentation = e.getPresentation();
    presentation.setIcon(ourIcon);
    final VirtualFile projectPath = getProjectPath(e);
    presentation.setEnabled(projectPath != null && fileSystemTree.isUnderRoots(projectPath));
  }

  @Nullable
  private static VirtualFile getProjectPath(AnActionEvent e) {
    final VirtualFile projectFileDir = e.getData(PlatformDataKeys.PROJECT_FILE_DIRECTORY);
    if (projectFileDir == null || !projectFileDir.isValid()) {
      return null;
    }
    return projectFileDir;
  }

}
