/*
 * $Id$
 *
 * License Agreement.
 *
 * Rich Faces - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */
package org.richfaces.cdk;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.collect.Lists;

/**
 * <p class="changed_added_4_0">
 * </p>
 *
 * @author asmirnov@exadel.com
 *
 */
public final class JavaSourceParser {
	final class MethodNameVisitor extends VoidVisitorAdapter<String> {
		boolean found = false;

		@Override
		public void visit(MethodDeclaration n, String arg) {
			if (arg.equals(n.getNameAsString())) {
				this.found = true;
			}
		}
	}

	final class ImportVisitor extends VoidVisitorAdapter<String> {
		boolean found = false;

		@Override
		public void visit(ImportDeclaration n, String arg) {
			if (arg.equals(n.getNameAsString())) {
				this.found = true;
			}
		}
	}

	final class FieldVisitor extends VoidVisitorAdapter<String> {
		boolean found = false;

		@Override
		public void visit(FieldDeclaration n, String arg) {
			for (VariableDeclarator declarator : n.getVariables()) {
				if (arg.equals(declarator.getNameAsString())) {
					this.found = true;
				}
			}
		}
	}

	private final CompilationUnit compiledSource;

	private JavaSourceParser(CompilationUnit compiledSource) {
		this.compiledSource = compiledSource;
	}

	public static JavaSourceParser parse(String javaSource) throws ParseException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(javaSource.getBytes());
		CompilationUnit compilationUnit = JavaParser.parse(inputStream);
		assertNotNull(compilationUnit);
		return new JavaSourceParser(compilationUnit);
	}

	public boolean containsMethod(String methodName) {
		MethodNameVisitor visitorAdapterExtension = new MethodNameVisitor();
		this.compiledSource.accept(visitorAdapterExtension, methodName);
		return visitorAdapterExtension.found;
	}

	public boolean containsImport(String packageName) {
		ImportVisitor visitorAdapterExtension = new ImportVisitor();
		this.compiledSource.accept(visitorAdapterExtension, packageName);
		return visitorAdapterExtension.found;
	}

	public boolean containsField(String fieldName) {
		FieldVisitor visitorAdapterExtension = new FieldVisitor();
		this.compiledSource.accept(visitorAdapterExtension, fieldName);
		return visitorAdapterExtension.found;
	}

	public String getPackageName() {
		return this.compiledSource.getPackageDeclaration().get().getNameAsString();
	}

	public Iterable<String> getClassNames() {
		List<String> classNames = Lists.newArrayList();
		List<TypeDeclaration<?>> types = compiledSource.getTypes();
		for (TypeDeclaration<?> typeDeclaration : types) {
			String className = typeDeclaration.getNameAsString();
			classNames.add(className);
		}
		return classNames;
	}
}
