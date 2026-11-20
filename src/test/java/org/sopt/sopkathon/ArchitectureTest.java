package org.sopt.sopkathon;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.sopt.sopkathon.global.code.BaseErrorCode;
import org.sopt.sopkathon.global.code.BaseSuccessCode;
import org.sopt.sopkathon.global.exception.GeneralException;
import org.sopt.sopkathon.global.swagger.SuccessCodeMarker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

public class ArchitectureTest {

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages("org.sopt.sopkathon");

    // Controller
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

                        var apiInterface = controller.getRawInterfaces().stream()
                                .filter(api -> api.getName().equals(apiName))
                                .findFirst();

                        if (apiInterface.isEmpty()) {
                            events.add(SimpleConditionEvent.violated(
                                    controller,
                                    controller.getName() + "는 " + apiName + "을 구현해야 합니다."
                            ));
                            return;
                        }

                        boolean hasRequestMapping = apiInterface.get().getMethods().stream()
                                .anyMatch(method -> method.isAnnotatedWith(RequestMapping.class)
                                        || method.isMetaAnnotatedWith(RequestMapping.class));

                        if (!hasRequestMapping) {
                            events.add(SimpleConditionEvent.violated(
                                    controller,
                                    apiName + "의 메서드에 요청 매핑이 필요합니다."
                            ));
                        }
                    }
                })
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }

    @Test
    void 컨트롤러의_public_메서드는_DTO를_반환하고_성공_어노테이션을_선언한다() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(RestController.class)
                .should(new ArchCondition<JavaClass>("DTO를 반환하고 성공 어노테이션을 선언한다") {
                    @Override
                    public void check(JavaClass controller, ConditionEvents events) {
                        controller.getMethods().stream()
                                .filter(method -> method.getModifiers().contains(JavaModifier.PUBLIC))
                                .forEach(method -> {
                                    if (!method.getRawReturnType().getPackageName().matches(".*\\.dto(\\..+)?")) {
                                        events.add(SimpleConditionEvent.violated(method,
                                                method.getFullName() + "은 DTO를 반환해야 합니다."));
                                    }

                                    boolean hasSuccessAnnotation = method.getAnnotations().stream()
                                            .anyMatch(annotation -> annotation.getRawType()
                                                    .isAnnotatedWith(SuccessCodeMarker.class));

                                    if (!hasSuccessAnnotation) {
                                        events.add(SimpleConditionEvent.violated(method,
                                                method.getFullName() + "에 성공 코드 어노테이션이 필요합니다."));
                                    }
                                });
                    }
                })
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }

    @Test
    void 요청_매핑은_컨트롤러_구현체에_선언하지_않는다() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(RestController.class)
                .should(new ArchCondition<JavaClass>("요청 매핑을 API 인터페이스에 둔다") {
                    @Override
                    public void check(JavaClass controller, ConditionEvents events) {
                        if (controller.isAnnotatedWith(RequestMapping.class)
                                || controller.isMetaAnnotatedWith(RequestMapping.class)) {
                            events.add(SimpleConditionEvent.violated(controller,
                                    controller.getName() + "의 요청 매핑을 API 인터페이스로 옮겨야 합니다."));
                        }

                        controller.getMethods().stream()
                                .filter(method -> method.isAnnotatedWith(RequestMapping.class)
                                        || method.isMetaAnnotatedWith(RequestMapping.class))
                                .forEach(method -> events.add(SimpleConditionEvent.violated(method,
                                        method.getFullName() + "의 요청 매핑을 API 인터페이스로 옮겨야 합니다.")));
                    }
                })
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }


    // Code
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


    // Service
    @Test
    void 서비스는_읽기_전용_트랜잭션을_기본으로_선언한다() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Service.class)
                .should(new ArchCondition<JavaClass>("@Transactional(readOnly = true)를 선언한다") {
                    @Override
                    public void check(JavaClass service, ConditionEvents events) {
                        if (!service.isAnnotatedWith(Transactional.class)
                                || !service.getAnnotationOfType(Transactional.class).readOnly()) {
                            events.add(SimpleConditionEvent.violated(service,
                                    service.getName() + "에 @Transactional(readOnly = true)가 필요합니다."));
                        }
                    }
                })
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }


    // Entity
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
    void 엔티티는_public_생성자와_setter를_노출하지_않는다() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Entity.class)
                .should(new ArchCondition<JavaClass>("public 생성자와 setter를 노출하지 않는다") {
                    @Override
                    public void check(JavaClass entity, ConditionEvents events) {
                        entity.getConstructors().stream()
                                .filter(constructor -> constructor.getModifiers().contains(JavaModifier.PUBLIC))
                                .forEach(constructor -> events.add(SimpleConditionEvent.violated(constructor,
                                        constructor.getFullName() + "은 public 생성자일 수 없습니다.")));

                        entity.getAllMethods().stream()
                                .filter(method -> method.getModifiers().contains(JavaModifier.PUBLIC))
                                .filter(method -> method.getName().matches("set[A-Z].*"))
                                .filter(method -> method.getRawParameterTypes().size() == 1)
                                .forEach(method -> events.add(SimpleConditionEvent.violated(method,
                                        method.getFullName() + "은 public setter일 수 없습니다.")));
                    }
                })
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }

    @Test
    void 엔티티의_단일_연관관계는_LAZY이고_enum은_STRING으로_저장한다() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Entity.class)
                .should(new ArchCondition<JavaClass>("연관관계는 LAZY로, enum은 STRING으로 매핑한다") {
                    @Override
                    public void check(JavaClass entity, ConditionEvents events) {
                        for (JavaField field : entity.getAllFields()) {
                            if (field.isAnnotatedWith(ManyToOne.class)
                                    && field.getAnnotationOfType(ManyToOne.class).fetch() != FetchType.LAZY) {
                                events.add(SimpleConditionEvent.violated(field,
                                        field.getFullName() + "의 @ManyToOne은 LAZY여야 합니다."));
                            }

                            if (field.isAnnotatedWith(OneToOne.class)
                                    && field.getAnnotationOfType(OneToOne.class).fetch() != FetchType.LAZY) {
                                events.add(SimpleConditionEvent.violated(field,
                                        field.getFullName() + "의 @OneToOne은 LAZY여야 합니다."));
                            }

                            if (field.getRawType().isEnum()
                                    && !field.getModifiers().contains(JavaModifier.STATIC)
                                    && !field.getModifiers().contains(JavaModifier.TRANSIENT)
                                    && !field.isAnnotatedWith(Transient.class)
                                    && (!field.isAnnotatedWith(Enumerated.class)
                                    || field.getAnnotationOfType(Enumerated.class).value() != EnumType.STRING)) {
                                events.add(SimpleConditionEvent.violated(field,
                                        field.getFullName() + "에 @Enumerated(EnumType.STRING)이 필요합니다."));
                            }
                        }
                    }
                })
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }


    // Exception
    @Test
    void 프로젝트의_예외_클래스는_GeneralException만_허용한다() {
        ArchRule rule = classes()
                .that().areAssignableTo(Exception.class)
                .should().haveFullyQualifiedName(GeneralException.class.getName())
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }

    @Test
    void global_밖에서는_GeneralException만_직접_생성한다() {
        ArchRule rule = classes()
                .that().resideOutsideOfPackage("..global..")
                .should(new ArchCondition<JavaClass>("GeneralException만 직접 생성한다") {
                    @Override
                    public void check(JavaClass type, ConditionEvents events) {
                        type.getConstructorCallsFromSelf().stream()
                                .filter(call -> call.getTargetOwner().isAssignableTo(Exception.class))
                                .filter(call -> !call.getTargetOwner().getName()
                                        .equals(GeneralException.class.getName()))
                                .forEach(call -> events.add(SimpleConditionEvent.violated(call,
                                        call.getDescription() + " 대신 GeneralException을 사용해야 합니다.")));
                    }
                })
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }


    // DTO
    @Test
    void DTO는_record로_작성한다() {
        ArchRule rule = classes()
                .that().resideInAnyPackage("..dto..")
                .and().areTopLevelClasses()
                .should().beAssignableTo(Record.class)
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }

    @Test
    void 요청과_응답_DTO의_모든_필드에_Schema를_붙인다() {
        ArchRule rule = classes()
                .that().resideInAnyPackage("..dto.request..", "..dto.response..")
                .and().areTopLevelClasses()
                .should(new ArchCondition<JavaClass>("모든 인스턴스 필드에 @Schema를 선언한다") {
                    @Override
                    public void check(JavaClass dto, ConditionEvents events) {
                        dto.getFields().stream()
                                .filter(field -> !field.getModifiers().contains(JavaModifier.STATIC))
                                .filter(field -> !field.isAnnotatedWith(Schema.class))
                                .forEach(field -> events.add(SimpleConditionEvent.violated(field,
                                        field.getFullName() + "에 @Schema가 필요합니다.")));
                    }
                })
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }

    @Test
    void DTO_필드는_엔티티를_포함하지_않는다() {
        ArchRule rule = classes()
                .that().resideInAPackage("..dto..")
                .and().areTopLevelClasses()
                .should(new ArchCondition<JavaClass>("필드 타입과 제네릭 인자에 엔티티를 포함하지 않는다") {
                    @Override
                    public void check(JavaClass dto, ConditionEvents events) {
                        for (JavaField field : dto.getFields()) {
                            boolean containsEntity = field.getType().getAllInvolvedRawTypes().stream()
                                    .anyMatch(type -> type.isAnnotatedWith(Entity.class));

                            if (containsEntity) {
                                events.add(SimpleConditionEvent.violated(field,
                                        field.getFullName() + "에 엔티티 타입을 사용할 수 없습니다."));
                            }
                        }
                    }
                })
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }


    // Dependency
    @Test
    void 컨트롤러는_리포지토리나_엔티티에_직접_의존하지_않는다() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..repository..")
                .orShould().dependOnClassesThat().areAnnotatedWith(Entity.class)
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }

    @Test
    void 서비스는_컨트롤러에_의존하지_않는다() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }

    @Test
    void 리포지토리는_서비스나_컨트롤러에_의존하지_않는다() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat().resideInAnyPackage("..service..", "..controller..")
                .allowEmptyShould(true);

        rule.check(CLASSES);
    }


    // Common
    @Test
    void 필드_주입을_사용하지_않는다() {
        ArchRule rule = NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

        rule.check(CLASSES);
    }
}
