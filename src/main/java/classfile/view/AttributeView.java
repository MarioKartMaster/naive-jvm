package classfile.view;

import clazz.attribute.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AttributeView extends AbstractView {

    private Attribute attribute;
    private ConstantPoolView constantPoolView;

    private interface ConvertToStringStrategy {
        String convert(Attribute attribute);
    }

    private Map<Class<?>, ConvertToStringStrategy> attributeStrategyMap;

    public AttributeView(Attribute attribute, ConstantPoolView constantPoolView) {
        this.attribute = attribute;
        this.constantPoolView = constantPoolView;
        attributeStrategyMap = new HashMap<>();
        attributeStrategyMap.put(ConstantValueAttribute.class, (info) -> {
            int index = ((ConstantValueAttribute) info).getConstantValueIndex();
            return constantPoolView.constantToString(index);
        });
        attributeStrategyMap.put(SourceFileAttribute.class, (info) -> {
            int index = ((SourceFileAttribute) info).getSourceFileIndex();
            return constantPoolView.constantToString(index);
        });
        attributeStrategyMap.put(CodeAttribute.class, (info) -> {
            CodeAttribute codeAttribute = ((CodeAttribute) info);
            StringBuilder result = new StringBuilder();
            result.append(String.format("max stack: %d\n", codeAttribute.getMaxStack()));
            result.append(String.format("max locals: %d\n", codeAttribute.getMaxLocals()));
            result.append("code:\n");
            byte[] code = codeAttribute.getCode();
            for (int i = 0; i < code.length; i++) {
                result.append(String.format("0X%X\n", code[i]));
            }
            return result.toString();
        });
        attributeStrategyMap.put(UnknowAttribute.class, (info) -> ((UnknowAttribute) info).getName());
    }

    public void show() {
        Optional<ConvertToStringStrategy> strategy = Optional.ofNullable(attributeStrategyMap.get(attribute.getClass()));
        Optional<String> result = strategy.map((s) -> s.convert(attribute));
        showString("attribute", attribute.getClass().getName() + " " + result.orElse("unkown"));
    }
}
