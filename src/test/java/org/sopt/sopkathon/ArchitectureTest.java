package org.sopt.sopkathon;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Test;
import org.sopt.sopkathon.global.code.BaseErrorCode;
import org.sopt.sopkathon.global.code.BaseSuccessCode;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ArchitectureTest {

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages("org.sopt.sopkathon");

    @Test
    void 컨트롤러는_같은_패키지의_API_인터페이스를_구현한다() {
        ArchRule rule = classes()
                .that().resideInAPackage("..controller")
                .and().areNotInterfaces()
                .and().haveSimpleNameEndingWith("Controller")
                .should(new ArchCondition<JavaClass>("같은 패키지의 API 인터페이스를 구현한다") {
                    @Override
                    public void check(JavaClass controller, ConditionEvents events) {
                        String controllerName = controller.getSimpleName();
                        String apiName = controller.getPackageName() + "."
                                + controllerName.substring(
                                0, controllerName.length() - "Controller".length())
                                + "Api";

                        boolean implementsApi = controller.getRawInterfaces().stream()
                                .anyMatch(api -> api.getName().equals(apiName));

                        if (!implementsApi) {
                            events.add(SimpleConditionEvent.violated(
                                    controller,
                                    controller.getName() + "는 " + apiName + "을 구현해야 합니다."
                            ));
                        }
                    }
                })
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }

    @Test
    void 성공_코드_enum은_BaseSuccessCode를_구현한다() {
        ArchRule rule = classes()
                .that().areEnums()
                .and().haveSimpleNameEndingWith("SuccessCode")
                .should().implement(BaseSuccessCode.class);

        rule.check(CLASSES);
    }

    @Test
    void 에러_코드_enum은_BaseErrorCode를_구현한다() {
        ArchRule rule = classes()
                .that().areEnums()
                .and().haveSimpleNameEndingWith("ErrorCode")
                .should().implement(BaseErrorCode.class);

        rule.check(CLASSES);
    }

    @Test
    void 엔티티는_protected_기본_생성자를_가진다() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Entity.class)
                .should(new ArchCondition<JavaClass>("protected 기본 생성자를 가진다") {
                    @Override
                    public void check(JavaClass entity, ConditionEvents events) {
                        boolean hasProtectedNoArgsConstructor = entity.getConstructors().stream()
                                .anyMatch(constructor -> constructor.getRawParameterTypes().isEmpty()
                                        && constructor.getModifiers().contains(JavaModifier.PROTECTED));

                        if (!hasProtectedNoArgsConstructor) {
                            events.add(SimpleConditionEvent.violated(
                                    entity,
                                    entity.getName() + "에 protected 기본 생성자가 필요합니다."
                            ));
                        }
                    }
                })
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }

    @Test
    void 요청과_응답_DTO는_record로_작성한다() {
        ArchRule rule = classes()
                .that().resideInAnyPackage("..dto.request..", "..dto.response..")
                .and().areTopLevelClasses()
                .should().beAssignableTo(Record.class)
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }
}
