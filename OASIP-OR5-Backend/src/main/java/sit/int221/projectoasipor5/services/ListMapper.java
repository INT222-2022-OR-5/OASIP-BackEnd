package sit.int221.projectoasipor5.services;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;

public class ListMapper {
    private static final ListMapper listMapper = new ListMapper();

    private ListMapper() {
    }

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass, ModelMapper modelMapper) {
        return (List)source.stream().map((entity) -> {
            return modelMapper.map(entity, targetClass);
        }).collect(Collectors.toList());
    }

    public static ListMapper getInstance() {
        return listMapper;
    }
}
