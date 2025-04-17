import React from 'react';
import { Grid, Typography, Box, Button, Pagination, Chip, Card, CardContent, CardActions } from '@mui/material';
import { styled } from '@mui/system';
import { Link as RouterLink } from 'react-router-dom';
import { CalendarToday, LocationOn, Person, Visibility, VisibilityOff } from '@mui/icons-material';

const StyledCard = styled(Card)(({ theme }) => ({
  height: '100%',
  display: 'flex',
  flexDirection: 'column',
  transition: 'transform 0.3s ease, box-shadow 0.3s ease',
  '&:hover': {
    transform: 'translateY(-5px)',
    boxShadow: theme.shadows[4],
  },
}));

const CardTitle = styled(Typography)({
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  display: '-webkit-box',
  WebkitLineClamp: 2,
  WebkitBoxOrient: 'vertical',
  fontWeight: 'bold',
});

const CardDescription = styled(Typography)({
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  display: '-webkit-box',
  WebkitLineClamp: 3,
  WebkitBoxOrient: 'vertical',
  marginTop: 8,
  height: '4.5em',
});

const DiaryList = ({ diaries, totalPages, currentPage, onPageChange, emptyMessage }) => {
  // HTML 태그 제거 함수
  const stripHtmlTags = (html) => {
    const doc = new DOMParser().parseFromString(html, 'text/html');
    return doc.body.textContent || '';
  };

  // 날짜 포맷팅 함수
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
  };

  const handlePageChange = (event, value) => {
    onPageChange(value - 1); // 페이지 인덱스는 0부터 시작하지만 UI는 1부터 표시
  };

  if (!diaries || diaries.length === 0) {
    return (
      <Box sx={{ textAlign: 'center', py: 5 }}>
        <Typography variant="h6" color="text.secondary">
          {emptyMessage || '등록된 다이어리가 없습니다.'}
        </Typography>
        <Button component={RouterLink} to="/diaries/new" variant="contained" sx={{ mt: 2 }}>
          새 다이어리 작성하기
        </Button>
      </Box>
    );
  }

  return (
    <Box>
      <Grid container spacing={3}>
        {diaries.map((diary) => (
          <Grid item xs={12} sm={6} md={4} key={diary.id}>
            <StyledCard>
              <CardContent sx={{ flexGrow: 1 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                  <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <CalendarToday fontSize="small" color="action" sx={{ mr: 0.5 }} />
                    <Typography variant="caption" color="text.secondary">
                      {formatDate(diary.startDate)} ~ {formatDate(diary.endDate)}
                    </Typography>
                  </Box>
                  {diary.isPublic ? (
                    <Visibility fontSize="small" color="action" />
                  ) : (
                    <VisibilityOff fontSize="small" color="action" />
                  )}
                </Box>
                
                <CardTitle variant="h6" gutterBottom>
                  {diary.title}
                </CardTitle>
                
                {diary.region && (
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <LocationOn fontSize="small" color="action" sx={{ mr: 0.5 }} />
                    <Typography variant="body2" color="text.secondary">
                      {diary.region}
                    </Typography>
                  </Box>
                )}
                
                <CardDescription variant="body2" color="text.secondary">
                  {stripHtmlTags(diary.content)}
                </CardDescription>
                
                <Box sx={{ display: 'flex', alignItems: 'center', mt: 2 }}>
                  <Person fontSize="small" color="action" sx={{ mr: 0.5 }} />
                  <Typography variant="body2" color="text.secondary">
                    {diary.author?.nickname || '익명'}
                  </Typography>
                </Box>
                
                {diary.tags && diary.tags.length > 0 && (
                  <Box sx={{ mt: 2, display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                    {diary.tags.slice(0, 3).map((tag) => (
                      <Chip key={tag} label={tag} size="small" variant="outlined" />
                    ))}
                    {diary.tags.length > 3 && (
                      <Chip label={`+${diary.tags.length - 3}`} size="small" />
                    )}
                  </Box>
                )}
              </CardContent>
              
              <CardActions>
                <Button
                  component={RouterLink}
                  to={`/diaries/${diary.id}`}
                  size="small"
                  fullWidth
                >
                  자세히 보기
                </Button>
              </CardActions>
            </StyledCard>
          </Grid>
        ))}
      </Grid>
      
      {totalPages > 1 && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <Pagination
            count={totalPages}
            page={currentPage + 1}
            onChange={handlePageChange}
            color="primary"
          />
        </Box>
      )}
    </Box>
  );
};

export default DiaryList;
