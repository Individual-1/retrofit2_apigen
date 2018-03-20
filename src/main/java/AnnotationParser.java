import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnotationParser {

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Usage: retrofit_parser <path_to_dir1> <optional_path_to_dir2> <etc>");
        }

        for (String dir : args) {
            File pd = new File(dir);

            if (!pd.isDirectory()) {
                System.out.println(dir + " is not a valid directory");
                continue;
            }

            new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
                AnnotationVisitor vis = new AnnotationVisitor();
                try {
                    vis.visit(JavaParser.parse(file), null);
                } catch (ParseProblemException e) {
                    assert true;
                } catch (IOException e) {
                    assert true;
                }

                List<Endpoint> eps = vis.getEndpoints();

                if (eps.size() > 0) {
                    System.out.println(path);
                    for (Endpoint ep : eps) {
                        System.out.println(ep);
                    }
                    System.out.println();
                }
            }).explore(pd);
        }
    }

    private static class AnnotationVisitor extends VoidVisitorAdapter {
        private List<Endpoint> eps;
        static private List<String> httpMethods =
                Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD");

        public AnnotationVisitor() {
            super();
            this.eps = new ArrayList<>();
        }

        public List<Endpoint> getEndpoints() {
            return this.eps;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration expr, Object arg) {
            if (expr.isInterface()) {
                List<MethodDeclaration> methods = expr.getMethods();
                for (MethodDeclaration method : methods) {
                    Endpoint ep = new Endpoint();
                    ep.setName(method.getNameAsString());
                    List<Parameter> pars = method.getParameters();
                    for (Parameter par : pars) {
                        if (par.getAnnotations().size() > 0) {
                            String key = "";
                            if (par.getAnnotation(0).isSingleMemberAnnotationExpr()) {
                                key = ((SingleMemberAnnotationExpr) par.getAnnotation(0)).getMemberValue().toString();
                            } else {
                                key = par.getAnnotation(0).getNameAsString();
                            }
                            Type val = par.getType();
                            ep.addParam(key, par.getAnnotation(0).getNameAsString(), val);
                        }
                    }
                    List<AnnotationExpr> annotations = method.getAnnotations();
                    for (AnnotationExpr annex : annotations) {
                        if (httpMethods.contains(annex.getNameAsString())) {
                            ep.setMethod(annex.getNameAsString());
                            if (annex.isSingleMemberAnnotationExpr()) {
                                ep.setURL(((SingleMemberAnnotationExpr) annex).getMemberValue().toString());
                            }
                        } else {
                            ep.addModifier(annex.getNameAsString());
                        }
                    }

                    if (ep.isValid()) {
                        eps.add(ep);
                    }
                }
            }
        }
    }
}
