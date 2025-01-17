/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.operator.window;

import io.trino.testing.MaterializedResult;
import io.trino.testing.QueryRunner;
import org.intellij.lang.annotations.Language;

import static io.airlift.testing.Assertions.assertEqualsIgnoreOrder;
import static java.lang.String.format;

public final class WindowAssertions
{
    private static final String VALUES = "" +
            "SELECT *\n" +
            "FROM (\n" +
            "  VALUES\n" +
            "    ( 1, 'O', '1996-01-02'),\n" +
            "    ( 2, 'O', '1996-12-01'),\n" +
            "    ( 3, 'F', '1993-10-14'),\n" +
            "    ( 4, 'O', '1995-10-11'),\n" +
            "    ( 5, 'F', '1994-07-30'),\n" +
            "    ( 6, 'F', '1992-02-21'),\n" +
            "    ( 7, 'O', '1996-01-10'),\n" +
            "    (32, 'O', '1995-07-16'),\n" +
            "    (33, 'F', '1993-10-27'),\n" +
            "    (34, 'O', '1998-07-21')\n" +
            ") AS orders (orderkey, orderstatus, orderdate)";

    private static final String VALUES_WITH_NULLS = "" +
            "SELECT *\n" +
            "FROM (\n" +
            "  VALUES\n" +
            "    ( 1,                   CAST(NULL AS VARCHAR), CAST(NULL AS VARCHAR)),\n" +
            "    ( 3,                   'F',                   '1993-10-14'),\n" +
            "    ( 5,                   'F',                   CAST(NULL AS VARCHAR)),\n" +
            "    ( 7,                   CAST(NULL AS VARCHAR), '1996-01-10'),\n" +
            "    (34,                   'O',                   '1998-07-21'),\n" +
            "    ( 6,                   'F',                   '1992-02-21'),\n" +
            "    (CAST(NULL AS BIGINT), 'F',                   '1993-10-27'),\n" +
            "    (CAST(NULL AS BIGINT), 'O',                   '1996-12-01'),\n" +
            "    (CAST(NULL AS BIGINT), CAST(NULL AS VARCHAR), CAST(NULL AS VARCHAR)),\n" +
            "    (CAST(NULL AS BIGINT), CAST(NULL AS VARCHAR), '1995-07-16')\n" +
            ") AS orders (orderkey, orderstatus, orderdate)";

    private static final String VALUES_WITH_NAN = "" +
            "SELECT *\n" +
            "FROM (\n" +
            "  VALUES\n" +
            "    ( 1, 'O', '1996-01-02'),\n" +
            "    ( 2, 'O', '1996-12-01'),\n" +
            "    ( 3, 'F', '1993-10-14'),\n" +
            "    ( 4, 'O', '1995-10-11'),\n" +
            "    ( nan(), 'F', '1994-07-30'),\n" +
            "    ( 6, 'F', '1992-02-21'),\n" +
            "    ( 7, 'O', '1996-01-10'),\n" +
            "    (32, 'O', '1995-07-16'),\n" +
            "    (33, 'F', '1993-10-27'),\n" +
            "    (34, 'O', '1998-07-21')\n" +
            ") AS orders (orderkey, orderstatus, orderdate)";

    private static final String VALUES_WITH_INFINITY = "" +
            "SELECT *\n" +
            "FROM (\n" +
            "  VALUES\n" +
            "    ( 1, 'O', '1996-01-02'),\n" +
            "    ( 2, 'O', '1996-12-01'),\n" +
            "    ( 3, 'F', '1993-10-14'),\n" +
            "    ( 4, 'O', '1995-10-11'),\n" +
            "    ( infinity(), 'F', '1994-07-30'),\n" +
            "    ( 6, 'F', '1992-02-21'),\n" +
            "    ( 7, 'O', '1996-01-10'),\n" +
            "    (32, 'O', '1995-07-16'),\n" +
            "    (33, 'F', '1993-10-27'),\n" +
            "    (34, 'O', '1998-07-21')\n" +
            ") AS orders (orderkey, orderstatus, orderdate)";

    private WindowAssertions() {}

    public static void assertWindowQuery(@Language("SQL") String sql, MaterializedResult expected, QueryRunner queryRunner)
    {
        @Language("SQL") String query = format("" +
                "SELECT orderkey, orderstatus,\n%s\n" +
                "FROM (%s) x", sql, VALUES);

        MaterializedResult actual = queryRunner.execute(query);
        assertEqualsIgnoreOrder(actual.getMaterializedRows(), expected.getMaterializedRows());
    }

    public static void assertWindowQueryWithNulls(@Language("SQL") String sql, MaterializedResult expected, QueryRunner queryRunner)
    {
        MaterializedResult actual = executeWindowQueryWithNulls(sql, queryRunner);
        assertEqualsIgnoreOrder(actual.getMaterializedRows(), expected.getMaterializedRows());
    }

    public static MaterializedResult executeWindowQueryWithNulls(@Language("SQL") String sql, QueryRunner queryRunner)
    {
        @Language("SQL") String query = format("" +
                "SELECT orderkey, orderstatus,\n%s\n" +
                "FROM (%s) x", sql, VALUES_WITH_NULLS);

        return queryRunner.execute(query);
    }

    public static void assertWindowQueryWithNan(@Language("SQL") String sql, MaterializedResult expected, QueryRunner queryRunner)
    {
        @Language("SQL") String query = format("" +
                "SELECT orderkey, orderstatus,\n%s\n" +
                "FROM (%s) x", sql, VALUES_WITH_NAN);

        MaterializedResult actual = queryRunner.execute(query);
        assertEqualsIgnoreOrder(actual.getMaterializedRows(), expected.getMaterializedRows());
    }

    public static void assertWindowQueryWithInfinity(@Language("SQL") String sql, MaterializedResult expected, QueryRunner queryRunner)
    {
        @Language("SQL") String query = format("" +
                "SELECT orderkey, orderstatus,\n%s\n" +
                "FROM (%s) x", sql, VALUES_WITH_INFINITY);

        MaterializedResult actual = queryRunner.execute(query);
        assertEqualsIgnoreOrder(actual.getMaterializedRows(), expected.getMaterializedRows());
    }
}
