/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.samsung.sec.dexter.core.filter;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

public class FalseAlarmConfigurationTree implements IFalseAlarmConfiguration {
    final private static Logger logger = Logger.getLogger(FalseAlarmConfigurationTree.class);
    private FalseAlarmNode root = new FalseAlarmNode("root");

    @Override
    public synchronized void addFalseAlarm(final DefectFilter filter) {
        FalseAlarmNode node = root.addChild(new FalseAlarmNode(filter.getToolName()));
        node = node.addChild(new FalseAlarmNode(filter.getLanguage()));
        node = node.addChild(new FalseAlarmNode(filter.getModulePath()));
        node = node.addChild(new FalseAlarmNode(filter.getFileName()));
        node = node.addChild(new FalseAlarmNode(filter.getClassName()));
        node = node.addChild(new FalseAlarmNode(filter.getMethodName()));
        node.addChild(new FalseAlarmNode(filter.getCheckerCode()));
    }

    @Override
    public void removeFalseAlarm(DefectFilter filter) {
        try {
            FalseAlarmNode toolNode = root.getChild(filter.getToolName());
            FalseAlarmNode languageNode = toolNode.getChild(filter.getLanguage());
            FalseAlarmNode modulePathNode = languageNode.getChild(filter.getModulePath());
            FalseAlarmNode fileNode = modulePathNode.getChild(filter.getFileName());
            FalseAlarmNode classNode = fileNode.getChild(filter.getClassName());
            FalseAlarmNode methodNode = classNode.getChild(filter.getMethodName());
            FalseAlarmNode checkerNode = methodNode.getChild(filter.getCheckerCode());

            methodNode.removeChild(checkerNode);
            if (methodNode.getChidrenSize() == 0) {
                classNode.removeChild(methodNode);
            }

            if (classNode.getChidrenSize() == 0) {
                fileNode.removeChild(classNode);
            }

            if (fileNode.getChidrenSize() == 0) {
                modulePathNode.removeChild(fileNode);
            }

            if (modulePathNode.getChidrenSize() == 0) {
                languageNode.removeChild(modulePathNode);
            }

            if (languageNode.getChidrenSize() == 0) {
                toolNode.removeChild(languageNode);
            }

            if (toolNode.getChidrenSize() == 0) {
                root.removeChild(toolNode);
            }
        } catch (DexterRuntimeException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param defect
     * @return
     */
    @Override
    public synchronized boolean isFalseAlarm(final Defect defect) {
        try {
            FalseAlarmNode toolNode = root.getChild(defect.getToolName());
            FalseAlarmNode languageNode = toolNode.getChild(defect.getLanguage());
            FalseAlarmNode modulePathNode = languageNode.getChild(defect.getModulePath());
            FalseAlarmNode fileNode = modulePathNode.getChild(defect.getFileName());
            FalseAlarmNode classNode = fileNode.getChild(defect.getClassName());
            FalseAlarmNode methodNode = classNode.getChild(defect.getMethodName());
            methodNode.getChild(defect.getCheckerCode());

            return true;
        } catch (DexterRuntimeException e) {
            return false;
        }
    }
}

class FalseAlarmNode {
    private String name;
    private Set<FalseAlarmNode> children = new HashSet<FalseAlarmNode>(0);

    public FalseAlarmNode(String name) {
        this.name = name;
    }

    public int getChidrenSize() {
        return children.size();
    }

    /**
     * @param name
     * @return
     * @throws DexterException
     */
    public FalseAlarmNode getChild(String name) {
        if (Strings.isNullOrEmpty(name)) {
            //name = "__NO_NAME__";
            name = "";
        }

        Iterator<FalseAlarmNode> iter = children.iterator();

        while (iter.hasNext()) {
            FalseAlarmNode node = iter.next();
            if (name.equals(node.getName())) {
                return node;
            }
        }

        throw new DexterRuntimeException("There is no child node that you want to find : " + name);
    }

    public FalseAlarmNode addChild(FalseAlarmNode node) {
        if (Strings.isNullOrEmpty(node.getName())) {
            //node.setName("__NO_NAME__");
            node.setName("");
        }

        Iterator<FalseAlarmNode> iter = this.children.iterator();
        while (iter.hasNext()) {
            FalseAlarmNode child = iter.next();
            if (node.getName().equals(child.getName())) {
                return child;
            }
        }

        children.add(node);
        return node;
    }

    public void removeChild(FalseAlarmNode node) {
        children.remove(node);
    }

    private void setName(String name) {
        this.name = name;
    }

    private String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        getNameRecursive(s);
        s.trimToSize();
        return s.toString();
    }

    public void getNameRecursive(StringBuilder s) {
        s.append(this.name).append("\n");
        if (this.children.size() == 0) {
            s.append("---------------------\n");
        }
        for (FalseAlarmNode c : this.children) {
            c.getNameRecursive(s);
        }
    }
}
