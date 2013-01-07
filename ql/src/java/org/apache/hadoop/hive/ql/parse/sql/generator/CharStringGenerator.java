/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.ql.parse.sql.generator;

import java.util.List;

import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.sql.SqlASTNode;
import org.apache.hadoop.hive.ql.parse.sql.SqlXlateUtil;
import org.apache.hadoop.hive.ql.parse.sql.TranslateContext;

import br.com.porcelli.parser.plsql.PantheraParser_PLSQLParser;

public class CharStringGenerator extends BaseHiveASTGenerator {

  @Override
  public boolean generate(ASTNode hiveRoot, SqlASTNode sqlRoot, ASTNode currentHiveNode,
      SqlASTNode currentSqlNode, TranslateContext context) throws Exception {
    ASTNode ret = SqlXlateUtil.newASTNode(HiveParser.StringLiteral, currentSqlNode.getText());

    //
    // Special handling for Date value expression.
    // If the immediate preceding sibling node is SQL92_RESERVED_DATE, then attach the
    // newly created StringLiteral HIVE AST node to the TOK_FUNCTION HIVE AST node which is
    // the last child of the current HIVE node at present.
    //
    int childIndex = currentSqlNode.getChildIndex();
    if (childIndex > 0) {
      SqlASTNode parent = (SqlASTNode) currentSqlNode.getParent();
      assert (parent != null);

      SqlASTNode preSibling = (SqlASTNode) parent.getChild(childIndex - 1);
      if (preSibling.getType() == PantheraParser_PLSQLParser.SQL92_RESERVED_DATE) {
        int childCount = currentHiveNode.getChildCount();
        assert (childCount > 0);
        ASTNode node = (ASTNode) currentHiveNode.getChild(childCount - 1);
        assert(node.getType() == HiveParser.TOK_FUNCTION && node.getChildCount() == 1);
        assert(((ASTNode) node.getChild(0)).getType() == HiveParser.TOK_DATE);
        attachHiveNode(hiveRoot, node, ret);
        return true;
      }
    }
    
    super.attachHiveNode(hiveRoot, currentHiveNode, ret);
    return true;
  }

}
