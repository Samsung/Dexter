/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
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
package com.samsung.sec.dexter.findbugs.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.util.DexterUtil;

public class JdtUtil {
	public synchronized static List<String> getTypeNameList(final String fileFullPath) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);

		final String sourcecode = DexterUtil.getContentsFromFile(fileFullPath, DexterConfig.getInstance().getSourceEncoding());
		parser.setSource(sourcecode.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		
		return findTypeNameList(cu);
	}

	private static List<String> findTypeNameList(final CompilationUnit cu) {
		final List<String> typeNameList = new ArrayList<String>();

		cu.accept(new ASTVisitor() {
			@Override
			public boolean visit(TypeDeclaration node) {
				typeNameList.add(node.getName().getIdentifier());
				return super.visit(node);
			}
		});
		
		return typeNameList;
    }

	public static List<String> getClassAndSubClassFullPathList(final String binFullPath,
			final String modulePath, final String sourceFileFullPath) {
		
		final List<String> classFileList = new ArrayList<String>();
		final List<String> typeNameList = getTypeNameList(sourceFileFullPath);
		final String baseClassPath = DexterUtil.addPaths(binFullPath, modulePath);
		final File[] subFileList = DexterUtil.getSubFiles(baseClassPath);
		
		for (final String typeName : typeNameList) {
			final String classFullPath = DexterUtil.addPaths(baseClassPath, typeName + ".class");
			classFileList.add(classFullPath);
			
			final String classFullPathWithoutExtension = classFullPath.substring(0, classFullPath.length() - 6) + "$";
			
			for(File sub : subFileList){
				String subPath = DexterUtil.refinePath(sub.getAbsolutePath()); 
				if(subPath.startsWith(classFullPathWithoutExtension)){
					classFileList.add(subPath);
				}
			}
		}
		
		return classFileList;
    }
}	
