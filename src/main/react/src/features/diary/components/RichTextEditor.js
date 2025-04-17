import React from 'react';
import ReactQuill from 'react-quill-with-table';
import 'react-quill-with-table/dist/quill.snow.css';
import { Box, Typography } from '@mui/material';
import { styled } from '@mui/system';

const StyledQuillWrapper = styled(Box)(({ theme, error }) => ({
  '.ql-container': {
    borderColor: error ? theme.palette.error.main : 'rgba(0, 0, 0, 0.23)',
    borderBottomLeftRadius: '4px',
    borderBottomRightRadius: '4px',
    minHeight: '200px',
    fontFamily: theme.typography.fontFamily,
  },
  '.ql-toolbar': {
    borderColor: error ? theme.palette.error.main : 'rgba(0, 0, 0, 0.23)',
    borderTopLeftRadius: '4px',
    borderTopRightRadius: '4px',
  },
  '&:hover .ql-toolbar, &:hover .ql-container': {
    borderColor: error ? theme.palette.error.main : 'rgba(0, 0, 0, 0.87)',
  },
  '.ql-editor': {
    minHeight: '200px',
    fontSize: '1rem',
  },
}));

const RichTextEditor = ({ value, onChange, error, helperText }) => {
  const modules = {
    toolbar: [
      [{ 'header': [1, 2, 3, false] }],
      ['bold', 'italic', 'underline', 'strike'],
      [{ 'color': [] }, { 'background': [] }],
      [{ 'list': 'ordered' }, { 'list': 'bullet' }],
      [{ 'align': [] }],
      ['link', 'image'],
      ['clean'],
      ['table']
    ],
    table: {},
    imageResize: {
      modules: ['Resize', 'DisplaySize']
    }
  };

  const formats = [
    'header',
    'bold', 'italic', 'underline', 'strike',
    'color', 'background',
    'list', 'bullet',
    'align',
    'link', 'image',
    'table', 'table-cell', 'table-row', 'table-header'
  ];

  const handleChange = (content) => {
    onChange(content);
  };

  return (
    <Box>
      <StyledQuillWrapper error={error}>
        <ReactQuill
          value={value || ''}
          onChange={handleChange}
          modules={modules}
          formats={formats}
          placeholder="여행의 추억을 마음껏 기록해보세요..."
        />
      </StyledQuillWrapper>
      {error && (
        <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 1.5 }}>
          {helperText}
        </Typography>
      )}
    </Box>
  );
};

export default RichTextEditor;
